package ris58h.goleador.commenter;

import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static final long DEFAULT_DELAY = 15;

    public static void main(String[] args) {
        init();

        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);
        long delay = appProperties.apply("commenter.delay").map(Long::parseLong).orElse(DEFAULT_DELAY);
        DataAccess dataAccess = new DataAccess(
                appProperties.apply("datasource.url").get(),
                appProperties.apply("datasource.username").get(),
                appProperties.apply("datasource.password").get());
        YouTubeCommenter youTubeCommenter = new YouTubeCommenter(
                appProperties.apply("google.app.clientId").get(),
                appProperties.apply("google.app.clientSecret").get(),
                appProperties.apply("google.app.refreshToken").get()
        );
        while (true) {
            try {
                dataAccess.processUncommentedVideos(videoTimes -> {
                    String videoId = videoTimes.videoId;
                    log.info("Process uncommented video " + videoId);
                    try {
                        String commentText = commentText(videoTimes.times);
                        String commentId = youTubeCommenter.comment(videoId, commentText);
                        dataAccess.updateCommentId(videoId, commentId);
                        log.info("Video " + videoId + " has been commented: commentId=" + commentId);
                    } catch (Exception e) {
                        log.error("Processing error: " + videoId + ": " + e.getMessage(), e);
                        try {
                            String error = e.getMessage();
                            if (error == null) {
                                error = e.getClass().getName();
                            }
                            dataAccess.updateError(videoId, error);
                        } catch (Exception ee) {
                            log.error("Updating error: " + ee.getMessage(), e);
                        }
                    }
                });
            } catch (Exception e) {
                log.error("Error: ", e);
            }

            if (delay > 0) {
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void init() {
        // Explicitly init Postgres driver to make psql jar be included in minimized app jar.
        try {
            if (!Driver.isRegistered()) {
                Driver.register();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
}
