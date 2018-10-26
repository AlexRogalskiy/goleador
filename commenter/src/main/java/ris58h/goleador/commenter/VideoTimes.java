package ris58h.goleador.commenter;

import java.util.List;

class VideoTimes {
    public final String videoId;
    public final List<Integer> times;

    public VideoTimes(String videoId, List<Integer> times) {
        this.videoId = videoId;
        this.times = times;
    }
}
