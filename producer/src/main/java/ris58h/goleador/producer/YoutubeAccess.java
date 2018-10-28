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
import java.util.stream.Stream;

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

    public List<String> filterVideoIds(List<String> videoIds,
                                       Long maxDuration,
                                       String definition) throws Exception {
        String part = "id";
        if (maxDuration != null && definition != null) {
            part += ",contentDetails";
        }
        String fields = "items(id";
        if (maxDuration != null) {
            fields += ",contentDetails/duration";
        }
        if (definition != null) {
            fields += ",contentDetails/definition";
        }
        fields += ")";
        YouTube.Videos.List list = youTube.videos().list(part)
                .setId(String.join(",", videoIds))
                .setFields(fields)
                .setKey(key);
        VideoListResponse videoListResponse = list.execute();
        List<Video> items = videoListResponse.getItems();
        Stream<Video> stream = items.stream();
        if (maxDuration != null) {
            stream = stream.filter(video -> parseDuration(video.getContentDetails().getDuration()) <= maxDuration);
        }
        if (definition != null) {
            stream = stream.filter(video -> definition.equals(video.getContentDetails().getDefinition()));
        }
        return stream.map(Video::getId).collect(Collectors.toList());
    }

    private static long parseDuration(String durationString) {
        Duration duration = Duration.parse(durationString);
        return duration.getSeconds();
    }
}
