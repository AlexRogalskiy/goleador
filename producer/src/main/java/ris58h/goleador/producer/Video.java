package ris58h.goleador.producer;

public class Video {
    public final String id;
    public final Long duration;
    public final String definition;
    public final Long publishedAt;

    public Video(String id, Long duration, String definition, Long publishedAt) {
        this.id = id;
        this.duration = duration;
        this.definition = definition;
        this.publishedAt = publishedAt;
    }
}
