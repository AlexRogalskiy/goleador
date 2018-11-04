package ris58h.goleador.commenter;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Commenter {
    private static final Logger log = LoggerFactory.getLogger(Commenter.class);

    private static final long DEFAULT_DELAY = 15;

    private final DataAccess dataAccess;
    private final YoutubeAccess youtubeAccess;

    private long delay = DEFAULT_DELAY;

    public Commenter(DataAccess dataAccess, YoutubeAccess youtubeAccess) {
        this.dataAccess = dataAccess;
        this.youtubeAccess = youtubeAccess;
    }

    public void start() {
        log.info("Start Commenter");
        new Thread(this::commenterLoop).start();
    }

    private void commenterLoop() {
        RetryPolicy commentRetryPolicy = new RetryPolicy()
                .retryOn(UnknownHostException.class)
                .withDelay(5, TimeUnit.SECONDS)
                .withMaxRetries(2);
        while (true) {
            try {
                dataAccess.processUncommentedVideos(videoTimes -> {
                    String videoId = videoTimes.videoId;
                    log.info("Process uncommented video " + videoId);
                    try {
                        String commentText = commentText(videoTimes.times);
                        String commentId = Failsafe.with(commentRetryPolicy)
                                .get(() -> youtubeAccess.comment(videoId, commentText));
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

    private static String commentText(List<Integer> times) {
        if (times.isEmpty()) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Голы (определено автоматически):\n");
        for (Integer time : times) {
            String timestamp = Utils.timestamp(time);
            sb.append(timestamp).append('\n');
        }
        sb.append("\n").append("Лайк, если определено верно. Дислайк, если всё плохо.");
        return sb.toString();
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
