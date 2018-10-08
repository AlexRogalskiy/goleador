package ris58h.goleador.commenter;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadSnippet;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    public static final long DEFAULT_DELAY = 30_000;

    public static void main(String[] args) {
        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);
        long delay = appProperties.apply("commenter.delay").map(Long::parseLong).orElse(DEFAULT_DELAY);
        Supplier<Connection> connectionSupplier = connectionSupplier(
                appProperties.apply("datasource.url").get(),
                appProperties.apply("datasource.username").get(),
                appProperties.apply("datasource.password").get());
        YouTube youTube = youTube(
                appProperties.apply("google.app.clientId").get(),
                appProperties.apply("google.app.clientSecret").get(),
                appProperties.apply("google.app.refreshToken").get()
        );
        while (true) {
            try {
                processUncommentedVideos(connectionSupplier, videoTimes -> {
                    String videoId = videoTimes.videoId;
                    log("Process uncommented video " + videoId);
                    try {
                        String commentText = commentText(videoTimes.times);
                        String commentId = comment(videoId, commentText, youTube);
                        updateCommentId(videoId, commentId, connectionSupplier);
                        log("Video " + videoId + " has been commented: commentId=" + commentId);
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
                });
            } catch (Exception e) {
                logError("Error: ", e);
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

    private static String commentText(List<Integer> times) {
        if (times.isEmpty()) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Goals:");
        for (Integer time : times) {
            String timestamp = Utils.timestamp(time);
            sb.append(' ').append(timestamp);
        }
        return sb.toString();
    }


    private static void updateCommentId(String videoId,
                                        String commentId,
                                        Supplier<Connection> connectionSupplier) throws Exception {
        try (
                Connection connection = connectionSupplier.get();
                PreparedStatement ps = connection.prepareStatement(" UPDATE video " +
                        " SET comment_id = ? " +
                        " WHERE video_id = ? "
                );
        ) {
            ps.setString(1, commentId);
            ps.setString(2, videoId);
            ps.executeUpdate();
        }
    }

    private static class VideoTimes {
        final String videoId;
        final List<Integer> times;

        public VideoTimes(String videoId, List<Integer> times) {
            this.videoId = videoId;
            this.times = times;
        }
    }

    private static YouTube youTube(String clientId, String clientSecret, String refreshToken) {
        NetHttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        Credential credential = new GoogleCredential.Builder().setTransport(transport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(refreshToken);
        return new YouTube.Builder(transport, jsonFactory, credential).build();
    }

    private static String comment(String videoId, String commentText, YouTube youtube) throws Exception {
        CommentThread commentThread = new CommentThread();
        commentThread.setSnippet(new CommentThreadSnippet()
                .setVideoId(videoId)
                .setTopLevelComment(new Comment()
                        .setSnippet(new CommentSnippet()
                                .setTextOriginal(commentText))));
        CommentThread videoCommentInsertResponse = youtube.commentThreads()
                .insert("snippet", commentThread).execute();
        return videoCommentInsertResponse.getSnippet().getTopLevelComment()
                .getId();
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

    private static void processUncommentedVideos(Supplier<Connection> connectionSupplier,
                                                 Consumer<VideoTimes> callback) throws Exception {
        try (Connection connection = connectionSupplier.get()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.setFetchSize(50);
                try (ResultSet rs = statement.executeQuery(" SELECT video_id, times " +
                        " FROM video " +
                        " WHERE times <> '' AND comment_id IS NULL ")) {
                    while (rs.next()) {
                        String videoId = rs.getString(1);
                        String timesString = rs.getString(2);
                        if (!timesString.isEmpty()) {
                            List<Integer> times = Stream.of(timesString.split(","))
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
                            callback.accept(new VideoTimes(videoId, times));
                        }
                    }
                }
            }
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
