package ris58h.goleador.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class TimestampsProcessorTest {

    @Test
    void scoreChangedFrames() {
        SortedMap<Integer, Score> scores = scores(Arrays.asList(
                Score.of(0, 0),
                Score.of(0, 0),
                Score.of(0, 4),
                Score.of(0, 1),
                Score.of(0, 1),
                Score.of(1, 1),
                Score.of(1, 1)
        ));
        List<Integer> changes = TimestampsProcessor.scoreChangedFrames(scores);
        assertIterableEquals(Arrays.asList(4, 6) ,changes);
    }

    private SortedMap<Integer, Score> scores(List<Score> scoreList) {
        SortedMap<Integer, Score> scores = new TreeMap<>();
        int index = 1;
        for (Score score : scoreList) {
            scores.put(index, score);
            index++;
        }
        return scores;
    }
}