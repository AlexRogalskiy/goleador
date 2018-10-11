package ris58h.goleador.worker;

import ris58h.goleador.core.Highlighter;
import ris58h.goleador.core.MainProcessor;
import ris58h.goleador.core.ScoreFrames;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class App {

    public static final String FORMAT = "136";
    public static final long DEFAULT_DELAY = 30_000;

    public static void main(String[] args) {
        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);
        long delay = appProperties.apply("worker.delay").map(Long::parseLong).orElse(DEFAULT_DELAY);
        Supplier<Connection> connectionSupplier = connectionSupplier(
                appProperties.apply("datasource.url").get(),
                appProperties.apply("datasource.username").get(),
                appProperties.apply("datasource.password").get());
        MainProcessor mainProcessor = new MainProcessor();
        try {
            mainProcessor.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        while (true) {
            String videoId = null;
            try {
                videoId = fetchUnprocessedVideoId(connectionSupplier);
            } catch (Exception e) {
                logError("Error while fetching unprocessed video: " + e.getMessage(), e);
            }
            if (videoId != null) {
                log("Start processing video " + videoId);
                Path tempDirectory = null;
                try {
                    tempDirectory = Files.createTempDirectory("goleador-");
                    long timeBefore = System.currentTimeMillis();
                    List<Integer> times = process(videoId, tempDirectory, mainProcessor);
                    long elapsedTime = System.currentTimeMillis() - timeBefore;
                    log("Video " + videoId + " has been processed in " + (elapsedTime / 1000) + " seconds");
                    updateVideoTimes(videoId, times, connectionSupplier);
                    log("Video times have been updated");
                } catch (Exception e) {
                    logError("Processing error: " + videoId + ": " + e.getMessage(), e);
                    try {
                        String error = e.getMessage();
                        if (error == null) {
                            error = e.getClass().getName();
                        }
                        updateError(videoId, error, connectionSupplier);
                    } catch (Exception ee) {
                        logError("Updating error: " + ee.getMessage(), e);
                    }
                }
                if (tempDirectory != null) {
                    try {
                        Utils.deleteDirectory(tempDirectory);
                    } catch (IOException e) {
                        logError("Error while deleting dir: " + e.getMessage(), e);
                    }
                }
            }

            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static Function<String, Optional<String>> appProperties(String propertiesPath) {
        Properties properties = new Properties();
        if (propertiesPath != null) {
            try {
                properties.load(new FileInputStream(propertiesPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return key -> {
            String property = properties.getProperty(key);
            if (property == null) {
                property = System.getProperty(key);
            }
            return Optional.ofNullable(property);
        };
    }

    private static List<Integer> process(String videoId,
                                         Path tempDirectory,
                                         MainProcessor mainProcessor) throws Exception {
        String videoUrl = VideoUrlFetcher.fetchFor(videoId, FORMAT);
        String dirName = tempDirectory.toAbsolutePath().toString();
        List<ScoreFrames> scoreFrames = mainProcessor.process(videoUrl, dirName);
        return Highlighter.times(scoreFrames);
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private static void logError(String message, Throwable e) {
        System.err.println(message);
        e.printStackTrace(System.err);
    }

    private static Supplier<Connection> connectionSupplier(String jdbcUrl, String username, String password) {
        return () -> {
            try {
                return DriverManager.getConnection(jdbcUrl, username, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static String fetchUnprocessedVideoId(Supplier<Connection> connectionSupplier) throws Exception {
        try (
                Connection connection = connectionSupplier.get();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(" SELECT video_id " +
                        " FROM video " +
                        " WHERE times IS NULL AND error IS NULL " +
                        " LIMIT 1 ");
        ) {
            String videoId = rs.next() ? rs.getString(1) : null;
            return videoId;
        }
    }

    private static void updateVideoTimes(String videoId,
                                         List<Integer> times,
                                         Supplier<Connection> connectionSupplier) throws Exception {
        String timeString = times.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        try (
                Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(" UPDATE video " +
                        " SET times = ? " +
                        " WHERE video_id = ? "
                );
        ) {
            ps.setString(1, timeString);
            ps.setString(2, videoId);
            ps.executeUpdate();
        }
    }

    private static void updateError(String videoId,
                                        String error,
                                        Supplier<Connection> connectionSupplier) throws Exception {
        if (error.length() > 255) {
            error = error.substring(0, 255);
        }
        try (
                Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(" UPDATE video " +
                        " SET error = ? " +
                        " WHERE video_id = ? "
                );
        ) {
            ps.setString(1, error);
            ps.setString(2, videoId);
            ps.executeUpdate();
        }
    }
}
