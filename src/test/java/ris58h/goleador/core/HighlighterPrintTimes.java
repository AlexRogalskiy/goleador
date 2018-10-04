package ris58h.goleador.core;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HighlighterPrintTimes {
    public static void main(String[] args) {
        for (Map.Entry<String, HighlighterTestData.ScoresAndTimes> entry : HighlighterTestData.DATA_BY_VIDEO.entrySet()) {
            String videoId = entry.getKey();
            HighlighterTestData.ScoresAndTimes value = entry.getValue();
            List<ScoreFrames> reducedScores = value.reducedScoreLines.stream()
                    .map(ScoreFrames::parseScoreRange)
                    .collect(Collectors.toList());
            List<Integer> times = Highlighter.times(reducedScores);
            System.out.println(videoId);
            for (Integer time : times) {
                System.out.println(time);
            }
        }
    }
}
