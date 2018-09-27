package ris58h.goleador.core;

import java.util.List;
import java.util.stream.Collectors;

public class Timestamper {
    public static List<String> toTimestamps(List<ScoreFrames> reducedScores) {
        return reducedScores.stream()
                .map(scoreFrames -> scoreFrames.first - 1) // The first frame is a thumbnail.
                .map(frame -> frame - 1) //TODO: Is the second one a thumbnail too?!
                .map(frame -> Integer.max(0, frame - 15)) // We interested in time that is seconds before score changed.
                .map(Utils::timestamp)
                .collect(Collectors.toList());
    }
}
