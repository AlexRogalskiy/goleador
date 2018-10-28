package ris58h.goleador.producer;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class YoutubeAccess {
    private static final long MAX_RESULTS = 50L;

    private static final NetHttpTransport TRANSPORT = new NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

    private final YouTube youTube;
    private final String key;

    public YoutubeAccess(String key) {
        this.youTube = new YouTube.Builder(TRANSPORT, JSON_FACTORY, noop -> {
        }).build();
        this.key = key;
    }

    public List<String> getNewVideoIds(String channelId, long after, long before) throws Exception {
        List<String> result = new ArrayList<>();
        String pageToken = null;
        do {
            YouTube.Search.List search = youTube.search().list("id")
                    .setType("video")
                    .setChannelId(channelId)
                    .setFields("items(id(videoId)),nextPageToken")
                    .setOrder("date")
                    .setPublishedAfter(new DateTime(after))
                    .setPublishedBefore(new DateTime(before))
                    .setMaxResults(MAX_RESULTS)
                    .setKey(key);

            if (pageToken != null) {
                search.setPageToken(pageToken);
            }

            SearchListResponse searchListResponse = search.execute();
            List<SearchResult> items = searchListResponse.getItems();
            items.stream()
                    .map(searchResult -> searchResult.getId().getVideoId())
                    .forEach(result::add);
            pageToken = searchListResponse.getNextPageToken();
        } while (pageToken != null);
        return result;
    }

    public List<String> filterVideoIds(List<String> videoIds, long maxDuration) throws Exception {
        YouTube.Videos.List list = youTube.videos().list("id,contentDetails")
                .setId(String.join(",", videoIds))
                .setFields("items(id,contentDetails(duration,definition))")
                .setKey(key);
        VideoListResponse videoListResponse = list.execute();
        List<Video> items = videoListResponse.getItems();
        return items.stream()
                .filter(video -> {
                    String durationString = video.getContentDetails().getDuration();
                    long duration = parseDuration(durationString);
                    return duration <= maxDuration;
                })
                .filter(video -> {
                    String definition = video.getContentDetails().getDefinition();
                    return "hd".equals(definition);
                })
                .map(Video::getId)
                .collect(Collectors.toList());
    }

    private static long parseDuration(String durationString) {
        Duration duration = Duration.parse(durationString);
        return duration.getSeconds();
    }
}
