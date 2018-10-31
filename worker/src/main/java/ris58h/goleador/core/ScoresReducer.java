package ris58h.goleador.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class ScoresReducer {
    public static List<ScoreFrames> reduceScores(SortedMap<Integer, Score> scores) {
        ArrayList<ScoreFrames> result = new ArrayList<>();
        ScoreFrames prev = null;
        for (Map.Entry<Integer, Score> entry : scores.entrySet()) {
            Integer frame = entry.getKey();
            Score score = entry.getValue();
            if (prev == null || !prev.score.equals(score) || prev.last + 1 != frame) {
                prev = new ScoreFrames(score, frame, frame);
                result.add(prev);
            } else {
                prev.last = frame;
            }
        }
        return result;
    }
}
