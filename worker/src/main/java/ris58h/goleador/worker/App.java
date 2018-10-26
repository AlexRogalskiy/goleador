package ris58h.goleador.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ris58h.goleador.core.Highlighter;
import ris58h.goleador.core.MainProcessor;
import ris58h.goleador.core.ScoreFrames;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static final String FORMAT = "136";
    private static final long DEFAULT_DELAY = 15;

    public static void main(String[] args) {
        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);
        long delay = appProperties.apply("worker.delay").map(Long::parseLong).orElse(DEFAULT_DELAY);
        DataAccess dataAccess = new DataAccess(
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
                videoId = dataAccess.fetchUnprocessedVideoId();
            } catch (Exception e) {
                log.error("Error while fetching unprocessed video: " + e.getMessage(), e);
            }
            if (videoId != null) {
                log.info("Start processing video " + videoId);
                Path tempDirectory = null;
                try {
                    tempDirectory = Files.createTempDirectory("goleador-");
                    long timeBefore = System.currentTimeMillis();
                    List<Integer> times = process(videoId, tempDirectory, mainProcessor);
                    long elapsedTime = System.currentTimeMillis() - timeBefore;
                    log.info("Video " + videoId + " has been processed in " + (elapsedTime / 1000) + " seconds");
                    dataAccess.updateVideoTimes(videoId, times);
                    log.info("Video times have been updated");
                } catch (Exception e) {
                    log.error("Processing error for " + videoId + " video: " + e.getMessage(), e);
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
                if (tempDirectory != null) {
                    try {
                        Utils.deleteDirectory(tempDirectory);
                    } catch (IOException e) {
                        log.error("Error while deleting dir: " + e.getMessage(), e);
                    }
                }
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
        String videoUrl = YoutubeDLVideoUrlFetcher.fetchFor(videoId, FORMAT);
        if (videoUrl == null) {
            throw new RuntimeException("No URL found for video " + videoId);
        }
        log.info("URL for " + videoId + " video: " + videoUrl);
        String dirName = tempDirectory.toAbsolutePath().toString();
        List<ScoreFrames> scoreFrames = mainProcessor.process(videoUrl, dirName);
        return Highlighter.times(scoreFrames);
    }
}
