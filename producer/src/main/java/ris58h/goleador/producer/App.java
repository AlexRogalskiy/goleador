package ris58h.goleador.producer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class App {
    private static final long DEFAULT_DELAY = 15;
//    private static final int DEFAULT_MAX_VIDEO_DURATION = 12*60;
    private static final int DEFAULT_CHANNEL_CHECK_INTERVAL = 30 * 60 * 1000;
    private static final long DEFAULT_NEW_CHANNEL_GAP = 24 * 60 * 60;

    public static void main(String[] args) {
        Function<String, Optional<String>> appProperties = appProperties(args.length == 1 ? args[0] : null);
        long delay = appProperties.apply("producer.delay").map(Long::parseLong).orElse(DEFAULT_DELAY);
        YoutubeAccess youtubeAccess = new YoutubeAccess(appProperties.apply("youtube.apiKey").get());
//        int maxVideoDuration = appProperties.apply("producer.maxVideoDuration").map(Integer::parseInt)
//                .orElse(DEFAULT_MAX_VIDEO_DURATION);
        DataAccess dataAccess = new DataAccess(
                appProperties.apply("datasource.url").get(),
                appProperties.apply("datasource.username").get(),
                appProperties.apply("datasource.password").get());

        while (true) {
            Collection<Channel> channels = null;
            try {
                long processedUntil = System.currentTimeMillis() - DEFAULT_CHANNEL_CHECK_INTERVAL;
                channels = dataAccess.loadChannelsForProcessing(processedUntil);
            } catch (Exception e) {
                logError("Can't load channels", e);
            }
            if (channels != null) {
                for (Channel channel : channels) {
                    String channelId = channel.channelId;
                    Long since = channel.since;
                    log("Process channel " + channelId);
                    long until = System.currentTimeMillis();
                    if (since == null) {
                        since = until - (DEFAULT_NEW_CHANNEL_GAP * 1000);
                    }

                    try {
                        List<String> videoIds = youtubeAccess.getNewVideoIds(channelId, since, until);
                        if (videoIds.isEmpty()) {
                            log("No new videos found on channel " + channelId);
                        } else {
                            log("Found " + videoIds.size() + " new videos on channel " + channelId);
//                            videoIds = youtubeAccess.filterVideoIds(videoIds, maxVideoDuration);
                            dataAccess.saveVideoIds(videoIds);
                        }
                        dataAccess.updateChannelSince(channelId, until);
                    } catch (Exception e) {
                        logError("Error for channel " + channelId, e);
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

    private static void log(String message) {
        System.out.println(message);
    }

    private static void logError(String message, Throwable e) {
        System.err.println(message);
        e.printStackTrace(System.err);
    }
}
