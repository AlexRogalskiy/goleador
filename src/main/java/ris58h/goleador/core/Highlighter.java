package ris58h.goleador.core;

import java.util.ArrayList;
import java.util.List;

public class Highlighter {
    public static List<Integer> times(List<ScoreFrames> reducedScores) {
        return times(reducedScores, 11);
    }

    static List<Integer> times(List<ScoreFrames> reducedScores, int back) {
        List<Integer> result = new ArrayList<>();
        ScoreFrames prev = null;
        for (ScoreFrames cur : reducedScores) {
            // Skip first one because it should be 0-0.
            if (prev != null) {
//                int highTime = cur.first - back; // min measure: 10275 for back=22
                int highTime = prev.last - back; // min measure: 264 for back=11

                // Time mustn't be negative.
                // The first frame is a thumbnail.
                // Video starts with 0 but frames with 1.
                result.add(Integer.max(0, highTime - 1 - 1));
            }
            prev = cur;
        }
        return result;
    }
}
