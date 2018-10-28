package ris58h.goleador.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    private static final long DEFAULT_DELAY = 15;
    private static final long DEFAULT_MAX_VIDEO_DURATION = 12*60;
    private static final long DEFAULT_CHANNEL_CHECK_INTERVAL = 30 * 60 * 1000;
    private static final long DEFAULT_NEW_CHANNEL_GAP = 24 * 60 * 60;

    private final YoutubeAccess youtubeAccess;
    private final DataAccess dataAccess;

    private long delay = DEFAULT_DELAY;
    private long maxVideoDuration = DEFAULT_MAX_VIDEO_DURATION;
    private long channelCheckInterval = DEFAULT_CHANNEL_CHECK_INTERVAL;
    private long newChannelGap = DEFAULT_NEW_CHANNEL_GAP;

    public Producer(YoutubeAccess youtubeAccess, DataAccess dataAccess) {
        this.youtubeAccess = youtubeAccess;
        this.dataAccess = dataAccess;
    }

    public void start() {
        while (true) {
            Collection<Channel> channels = null;
            try {
                long processedUntil = System.currentTimeMillis() - channelCheckInterval;
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

            if (delay > 0) {
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void processChannel(Channel channel) throws Exception {
        String channelId = channel.channelId;
        log.info("Process channel " + channelId);
        Long since = channel.since;
        long until = System.currentTimeMillis();
        if (since == null) {
            since = until - (newChannelGap * 1000);
        }
        log.info("Fetch new videos from channel " + channelId +  " since " + since + " until " + until);
        List<String> videoIds = youtubeAccess.getNewVideoIds(channelId, since, until);
        if (videoIds.isEmpty()) {
            log.info("No new videos found on channel " + channelId);
        } else {
            log.info("Found " + videoIds.size() + " new videos on channel " + channelId);
            List<String> filteredVideoIds = youtubeAccess.filterVideoIds(videoIds, maxVideoDuration, "hd");
            if (videoIds.size() != filteredVideoIds.size()) {
                HashSet<String> filteredOut = new HashSet<>(videoIds);
                filteredOut.removeAll(filteredVideoIds);
                log.info(filteredOut.size() + " videos were filtered out: " + filteredOut);
            }
            dataAccess.saveVideoIds(filteredVideoIds);
        }
        dataAccess.updateChannelSince(channelId, until);
    }

    public void setDelay(long delay) {
        this.delay = delay;
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
}
