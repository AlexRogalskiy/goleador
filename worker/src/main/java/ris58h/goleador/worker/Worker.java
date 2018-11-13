package ris58h.goleador.worker;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ris58h.goleador.core.Highlighter;
import ris58h.goleador.core.MainProcessor;
import ris58h.goleador.core.ScoreFrames;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Worker {
    private static final Logger log = LoggerFactory.getLogger(Worker.class);

    private static final String FORMAT = "136";
    private static final String TASK_QUEUE_NAME = "task_queue";
    private static final String RESULT_QUEUE_NAME = "result_queue";

    private final String uri;
    private final MainProcessor mainProcessor;

    public Worker(String uri) {
        this.uri = uri;
        mainProcessor = new MainProcessor();
    }

    public void init() throws Exception {
        mainProcessor.init();
    }

    public void start() throws Exception {
        log.info("Start Worker");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        channel.queueDeclare(RESULT_QUEUE_NAME, true, false, false, null);
        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String videoId = new String(body, StandardCharsets.UTF_8);
                System.out.println("Received task: " + videoId);
                List<Integer> times = Collections.emptyList();
                String error = null;
                try {
                    long timeBefore = System.currentTimeMillis();
                    times = process(videoId);
                    long elapsedTime = System.currentTimeMillis() - timeBefore;
                    log.info("Video " + videoId + " has been processed in " + (elapsedTime / 1000) + " seconds");
                    if (times.isEmpty()) {
                        log.info("No times found for video " + videoId);
                    } else {
                        log.info("Times found for video " + videoId + ": " + times);
                    }
                } catch (Throwable e) {
                    log.error("Error for video " + videoId, e);
                    error = e.getMessage();
                    if (error == null) {
                        error = e.getClass().getName();
                    }
                }
                String response = videoId + ":" + (error == null ? "ok:" + timesString(times) : "error:" + error);
                channel.basicPublish("", RESULT_QUEUE_NAME, null, response.getBytes());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
    }

    private static String timesString(List<Integer> times) {
        return times.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    private List<Integer> process(String videoId) throws Exception {
        Path tempDirectory = null;
        try {
            tempDirectory = Files.createTempDirectory("goleador-");
            return process(videoId, tempDirectory, mainProcessor);
        } finally {
            if (tempDirectory != null) {
                Utils.deleteDirectory(tempDirectory);
            }
        }
    }

    private static List<Integer> process(String videoId,
                                         Path tempDirectory,
                                         MainProcessor mainProcessor) throws Exception {
        log.info("Process video " + videoId);
        String dirName = tempDirectory.toAbsolutePath().toString();
        String target = dirName + "/" + videoId + ".mp4";
        long t0 = System.currentTimeMillis();
        YoutubeDL.download(videoId, FORMAT, target);
        long t1 = System.currentTimeMillis();
        log.info("Video " + videoId + " has been downloaded in " + ((t1 - t0) / 1000) + " seconds");
        List<ScoreFrames> scoreFrames = mainProcessor.process(target, dirName);
        long t2 = System.currentTimeMillis();
        log.info("Video file " + videoId + " has been processed in " + ((t2 - t1) / 1000) + " seconds");
        return Highlighter.times(scoreFrames);
    }
}
