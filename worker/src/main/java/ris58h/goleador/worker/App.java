package ris58h.goleador.worker;

import ris58h.goleador.processor.Highlighter;
import ris58h.goleador.processor.MainProcessor;
import ris58h.goleador.processor.ScoreFrames;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class App {

    public static final String FORMAT = "136";
    public static final int DEFAULT_DELAY = 30_000;

    public static void main(String[] args) {
        Function<String, String> appProperties = appProperties(args.length == 1 ? args[0] : null);
        String delayString = appProperties.apply("delay");
        long delay = delayString == null ? DEFAULT_DELAY : Long.parseLong(delayString);
        Supplier<Connection> connectionSupplier = connectionSupplier(
                appProperties.apply("datasource.url"),
                appProperties.apply("datasource.username"),
                appProperties.apply("datasource.password"));
        while (true) {
            long timeBefore = System.currentTimeMillis();

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
                    List<Integer> times = process(videoId, tempDirectory);
                    updateVideoTimes(videoId, times, connectionSupplier);
                } catch (Exception e) {
                    logError("Error while processing video " + videoId + ": " + e.getMessage(), e);
                    try {
                        String workerError = e.getMessage();
                        if (workerError == null) {
                            workerError = e.getClass().getName();
                        }
                        updateVideoWorkerError(videoId, workerError, connectionSupplier);
                    } catch (Exception ee) {
                        logError("Error while updating video processing error: " + ee.getMessage(), e);
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

            long elapsedTime = System.currentTimeMillis() - timeBefore;
            if (videoId != null) {
                log("Elapsed processing time for video " + videoId + ": " + (elapsedTime / 1000));
            }
            long waitTime = delay - elapsedTime;
            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static Function<String, String> appProperties(String propertiesPath) {
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
            return property;
        };
    }

    private static List<Integer> process(String videoId, Path tempDirectory) throws Exception {
        String videoUrl = VideoUrlFetcher.fetchFor(videoId, FORMAT);
        String dirName = tempDirectory.toAbsolutePath().toString();
        List<ScoreFrames> scoreFrames = MainProcessor.process(videoUrl, dirName);
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
                        " WHERE times IS NULL AND worker_error IS NULL " +
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

    private static void updateVideoWorkerError(String videoId,
                                               String workerError,
                                               Supplier<Connection> connectionSupplier) throws Exception {
        if (workerError.length() > 255) {
            workerError = workerError.substring(0, 255);
        }
        try (
                Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(" UPDATE video " +
                        " SET worker_error = ? " +
                        " WHERE video_id = ? "
                );
        ) {
            ps.setString(1, workerError);
            ps.setString(2, videoId);
            ps.executeUpdate();
        }
    }
}
