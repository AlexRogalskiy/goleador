package ris58h.goleador.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ris58h.goleador.core.Highlighter;
import ris58h.goleador.core.MainProcessor;
import ris58h.goleador.core.ScoreFrames;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Worker {
    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    private static final String FORMAT = "136";
    private static final long DEFAULT_DELAY = 15;

    private final DataAccess dataAccess;
    private final MainProcessor mainProcessor;

    private long delay = DEFAULT_DELAY;

    public Worker(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        mainProcessor = new MainProcessor();
    }

    public void init() throws Exception {
        mainProcessor.init();
    }

    public void start() {
        log.info("Start Worker");
        new Thread(this::workerLoop).start();
    }

    private void workerLoop() {
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
                    if (times.isEmpty()) {
                        log.info("No times found for video " + videoId);
                    } else {
                        log.info("Times found for video " + videoId + ": " + times);
                    }
                    dataAccess.updateVideoTimes(videoId, times);
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

    private static List<Integer> process(String videoId,
                                         Path tempDirectory,
                                         MainProcessor mainProcessor) throws Exception {
        String dirName = tempDirectory.toAbsolutePath().toString();
        String target = dirName + "/" + videoId + ".mp4";
        long t0 = System.currentTimeMillis();
        YoutubeDL.download(videoId, FORMAT, target);
        long t1 = System.currentTimeMillis();
        log.info("Video " + videoId + " has been downloaded in " + (t1 - t0 / 1000) + " seconds");
        List<ScoreFrames> scoreFrames = mainProcessor.process(target, dirName);
        long t2 = System.currentTimeMillis();
        log.info("Video file " + videoId + " has been processed in " + (t2 - t1 / 1000) + " seconds");
        return Highlighter.times(scoreFrames);
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
