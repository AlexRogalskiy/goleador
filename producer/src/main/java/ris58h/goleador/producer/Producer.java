package ris58h.goleador.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    private static final long DEFAULT_CHECK_CHANNELS_DELAY = 15;
    private static final long DEFAULT_MAX_VIDEO_DURATION = 12 * 60;
    private static final long DEFAULT_CHANNEL_CHECK_INTERVAL = 5 * 60;
    private static final long DEFAULT_NEW_CHANNEL_GAP = 24 * 60 * 60;
    private static final long DEFAULT_CHECK_DEFINITION_DELAY = 5 * 60;
    private static final long DEFAULT_DEFINITION_GAP = 60 * 60;

    private static final Pattern SPLIT_TO_WORDS_PATTERN = Pattern.compile("\\W+");
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "выпуск",
            "выступления",
            "лучшие",
            "программа",
            "против",
            "тура"
    ));

    private final YoutubeAccess youtubeAccess;
    private final DataAccess dataAccess;

    private long checkChannelsDelay = DEFAULT_CHECK_CHANNELS_DELAY;
    private long maxVideoDuration = DEFAULT_MAX_VIDEO_DURATION;
    private long channelCheckInterval = DEFAULT_CHANNEL_CHECK_INTERVAL;
    private long newChannelGap = DEFAULT_NEW_CHANNEL_GAP;
    private long checkDefinitionDelay = DEFAULT_CHECK_DEFINITION_DELAY;
    private long definitionGap = DEFAULT_DEFINITION_GAP;

    public Producer(YoutubeAccess youtubeAccess, DataAccess dataAccess) {
        this.youtubeAccess = youtubeAccess;
        this.dataAccess = dataAccess;
    }

    public void start() {
        log.info("Start Producer");
        new Thread(this::checkChannelsLoop).start();
        new Thread(this::checkSDVideosLoop).start();
    }

    private void checkChannelsLoop() {
        while (true) {
            Collection<Channel> channels = null;
            try {
                long processedUntil = System.currentTimeMillis() - (1000 * channelCheckInterval);
                channels = dataAccess.loadChannelsForProcessing(processedUntil);
            } catch (Exception e) {
                log.error("Can't load channels", e);
            }
            if (channels != null) {
                for (Channel channel : channels) {
                    try {
                        processChannel(channel);
                    } catch (Exception e) {
                        log.error("Error for channel " + channel.channelId, e);
                    }
                }
            }

            if (checkChannelsDelay > 0) {
                try {
                    Thread.sleep(checkChannelsDelay * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void processChannel(Channel channel) throws Exception {
        String channelId = channel.channelId;
        Long since = channel.since;
        long until = System.currentTimeMillis();
        if (since == null) {
            since = until - (newChannelGap * 1000);
        }
        log.info("Check for new videos on channel " + channelId +  " since " + since + " until " + until);
        List<String> videoIds = youtubeAccess.getVideoIds(channelId, since, until);
        if (!videoIds.isEmpty()) {
            log.info("Found " + videoIds.size() + " new videos on channel " + channelId + ": " + videoIds);
            List<Video> videos = youtubeAccess.getVideos(videoIds);
            List<Video> filteredVideos = new ArrayList<>();
            for (Video video : videos) {
                String reason = filterOut(video);
                if (reason == null) {
                    filteredVideos.add(video);
                } else {
                    log.info("Filter out " + video.id + ": " + reason);
                }
            }
            if (filteredVideos.isEmpty()) {
                log.info("No videos left after filtering");
            } else {
                if (filteredVideos.size() != videos.size()) {
                    List<String> filteredVideoIds = filteredVideos.stream().map(v -> v.id).collect(Collectors.toList());
                    log.info(filteredVideos.size() + " videos left after filtering: " + filteredVideoIds);
                }
                dataAccess.saveVideos(filteredVideos);
            }
        }
        dataAccess.updateChannelSince(channelId, until);
    }

    private String filterOut(Video video) {
        if (video.duration > maxVideoDuration) {
            return "too long (" + video.duration + " > " + maxVideoDuration + ")";
        }
        String lcTitle = video.title.toLowerCase();
        for (Iterator<String> iterator = SPLIT_TO_WORDS_PATTERN.splitAsStream(lcTitle).iterator(); iterator.hasNext();) {
            String word = iterator.next();
            if (STOP_WORDS.contains(word)) {
                return "title contains '" + word + "'";
            }
        }
        return null;
    }

    private void checkSDVideosLoop() {
        while (true) {
            try {
                long publishedAfter = System.currentTimeMillis() - (definitionGap * 1000);
                Collection<String> sdVideoIds = dataAccess.loadSDVideoIds(publishedAfter);
                if (!sdVideoIds.isEmpty()) {
                    log.info("Found " + sdVideoIds.size() + " SD videos: " + sdVideoIds);
                    List<Video> videos = youtubeAccess.getVideos(sdVideoIds); //TODO: load id & definition only
                    List<String> hdVideoIds = videos.stream()
                            .filter(video -> video.definition.equals("hd"))
                            .map(video -> video.id)
                            .collect(Collectors.toList());
                    if (!hdVideoIds.isEmpty()) {
                        log.info(hdVideoIds.size() + " SD videos became HD: " + hdVideoIds);
                        dataAccess.updateToHD(hdVideoIds);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing SD videos", e);
            }

            if (checkDefinitionDelay > 0) {
                try {
                    Thread.sleep(checkDefinitionDelay * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setCheckChannelsDelay(long checkChannelsDelay) {
        this.checkChannelsDelay = checkChannelsDelay;
    }

    public void setMaxVideoDuration(long maxVideoDuration) {
        this.maxVideoDuration = maxVideoDuration;
    }

    public void setChannelCheckInterval(long channelCheckInterval) {
        this.channelCheckInterval = channelCheckInterval;
    }

    public void setNewChannelGap(long newChannelGap) {
        this.newChannelGap = newChannelGap;
    }

    public void setCheckDefinitionDelay(long checkDefinitionDelay) {
        this.checkDefinitionDelay = checkDefinitionDelay;
    }

    public void setDefinitionGap(long definitionGap) {
        this.definitionGap = definitionGap;
    }
}
