package ris58h.goleador.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class ReduceScoresProcessorTest {

    @Test
    void reduceScores() {
        SortedMap<Integer, Score> scores = scores(Arrays.asList(
                null,
                null,
                null,
                Score.of(0, 0),
                Score.of(0, 4),
                Score.of(0, 0),
                null,
                null,
                null,
                Score.of(0, 1),
                Score.of(0, 1),
                Score.of(0, 1),
                null,
                null,
                null,
                Score.of(1, 1),
                Score.of(1, 1),
                Score.of(1, 1)
        ));
        List<ScoreFrames> reducedScores = ReduceScoresProcessor.reduceScores(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 4, 6),
                new ScoreFrames(Score.of(0, 1), 10, 12),
                new ScoreFrames(Score.of(1, 1), 16, 18)
        );
        assertIterableEquals(expected, reducedScores);
    }

    private SortedMap<Integer, Score> scores(List<Score> scoreList) {
        SortedMap<Integer, Score> scores = new TreeMap<>();
        int index = 1;
        for (Score score : scoreList) {
            if (score != null) {
                scores.put(index, score);
            }
            index++;
        }
        return scores;
    }
}