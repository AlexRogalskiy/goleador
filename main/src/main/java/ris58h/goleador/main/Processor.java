package ris58h.goleador.main;

import com.rabbitmq.client.*;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Processor {
    private static final Logger log = LoggerFactory.getLogger(Processor.class);

    private static final String TASK_QUEUE_NAME = "task_queue";
    private static final String RESULT_QUEUE_NAME = "result_queue";

    private static final long DEFAULT_DELAY = 15;
    private static final long DEFAULT_PROCESSING_GAP = 60 * 60;

    private final DataAccess dataAccess;
    private final String uri;

    private long delay = DEFAULT_DELAY;
    private long processingGap = DEFAULT_PROCESSING_GAP;

    public Processor(DataAccess dataAccess, String uri) {
        this.dataAccess = dataAccess;
        this.uri = uri;
    }

    public void start() throws Exception {
        log.info("Start Processor");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            channel.queueDeclare(RESULT_QUEUE_NAME, true, false, false, null);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        String result = new String(body, StandardCharsets.UTF_8);
                        log.info("Received result: " + result);
                        String[] split = result.split(":");
                        String videoId = split[0];
                        String status = split[1];
                        String value = split.length <= 2 ? "" : split[2];
                        if (status.equals("ok")) {
                            List<Integer> times = value.isEmpty() ? Collections.emptyList() : Stream.of(value.split(","))
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());
                            dataAccess.updateVideoTimes(videoId, times);
                        } else if (status.equals("error")) {
                            dataAccess.updateError(videoId, value);
                        }
                    } catch (Exception e) {
                        log.error("Response error", e);
                    }
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            channel.basicConsume(RESULT_QUEUE_NAME, false, consumer);

            while (true) {
                try {
                    long processingStartedBefore = System.currentTimeMillis() - (processingGap * 1000);
                    dataAccess.processUnprocessedVideos(processingStartedBefore, videoId -> {
                        log.info("Process unprocessed video " + videoId);
                        try {
                            channel.basicPublish("", TASK_QUEUE_NAME, null, videoId.getBytes(StandardCharsets.UTF_8));
                            dataAccess.updateProcessingStartTime(videoId, System.currentTimeMillis());
                        } catch (Exception e) {
                            log.error("Error for unprocessed video " + videoId, e);
                        }
                    });
                } catch (Exception e) {
                    log.error("Can't process unprocessed videos", e);
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
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setProcessingGap(long processingGap) {
        this.processingGap = processingGap;
    }
}
