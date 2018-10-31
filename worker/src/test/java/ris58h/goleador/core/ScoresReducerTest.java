package ris58h.goleador.core;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class ScoresReducerTest {

    @Test
    void gaps() {
        SortedMap<Integer, Score> scores = scores(Arrays.asList(
                Score.of(0, 0),
                Score.of(0, 0),
                Score.of(0, 0),
                null,
                null,
                null,
                Score.of(0, 0),
                Score.of(0, 0),
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
                Score.of(0, 1),
                Score.of(0, 1),
                Score.of(0, 1)
        ));
        List<ScoreFrames> reducedScores = ScoresReducer.reduceScores(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 3),
                new ScoreFrames(Score.of(0, 0), 7, 9),
                new ScoreFrames(Score.of(0, 1), 13, 15),
                new ScoreFrames(Score.of(0, 1), 19, 21)
        );
        assertIterableEquals(expected, reducedScores);
    }

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
                Score.of(1, 1),
                Score.of(2, 6)
        ));
        List<ScoreFrames> reducedScores = ScoresReducer.reduceScores(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 4, 4),
                new ScoreFrames(Score.of(0, 4), 5, 5),
                new ScoreFrames(Score.of(0, 0), 6, 6),
                new ScoreFrames(Score.of(0, 1), 10, 12),
                new ScoreFrames(Score.of(1, 1), 16, 18),
                new ScoreFrames(Score.of(2, 6), 19, 19)
        );
        assertIterableEquals(expected, reducedScores);
    }

    @Test
    void reduceScores2() {
        SortedMap<Integer, Score> scores = scores(Arrays.asList(
                Score.of(0, 0),
                Score.of(0, 0),
                Score.of(0, 1),
                Score.of(0, 0),
                Score.of(0, 0),
                Score.of(0, 0),
                Score.of(1, 0)
        ));
        List<ScoreFrames> reducedScores = ScoresReducer.reduceScores(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 2),
                new ScoreFrames(Score.of(0, 1), 3, 3),
                new ScoreFrames(Score.of(0, 0), 4, 6),
                new ScoreFrames(Score.of(1, 0), 7, 7)
        );
        assertIterableEquals(expected, reducedScores);
    }

    @Test
    void reduceScores3() {
        SortedMap<Integer, Score> scores = scores(Arrays.asList(
                Score.of(0, 0),
                Score.of(0, 0),
                Score.of(0, 0),
                Score.of(0, 1),
                Score.of(0, 1),
                Score.of(0, 1),
                Score.of(0, 0),
                Score.of(0, 2),
                Score.of(0, 2),
                Score.of(0, 2)
        ));
        List<ScoreFrames> reducedScores = ScoresReducer.reduceScores(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 3),
                new ScoreFrames(Score.of(0, 1), 4, 6),
                new ScoreFrames(Score.of(0, 0), 7, 7),
                new ScoreFrames(Score.of(0, 2), 8, 10)
        );
        assertIterableEquals(expected, reducedScores);
    }

    @Nested
    class ScoresThatDontStartWith_0_0 {
        @Test
        void test1() {
            SortedMap<Integer, Score> scores = scores(Arrays.asList(
                    Score.of(0, 1)
            ));
            List<ScoreFrames> reducedScores = ScoresReducer.reduceScores(scores);
            List<ScoreFrames> expected = Arrays.asList(
                    new ScoreFrames(Score.of(0, 1), 1, 1)
            );
            assertIterableEquals(expected, reducedScores);
        }

        @Test
        void test2() {
            SortedMap<Integer, Score> scores = scores(Arrays.asList(
                    Score.of(1, 1)
            ));
            List<ScoreFrames> reducedScores = ScoresReducer.reduceScores(scores);
            List<ScoreFrames> expected = Arrays.asList(
                    new ScoreFrames(Score.of(1, 1), 1, 1)
            );
            assertIterableEquals(expected, reducedScores);
        }

        @Test
        void test3() {
            SortedMap<Integer, Score> scores = scores(Arrays.asList(
                    Score.of(6, 4),
                    Score.of(0, 0),
                    Score.of(0, 0),
                    Score.of(0, 0),
                    Score.of(0, 1),
                    Score.of(0, 1),
                    Score.of(0, 1),
                    Score.of(0, 1),
                    Score.of(0, 2),
                    Score.of(0, 2),
                    Score.of(0, 2),
                    Score.of(0, 2)
            ));
            List<ScoreFrames> reducedScores = ScoresReducer.reduceScores(scores);
            List<ScoreFrames> expected = Arrays.asList(
                    new ScoreFrames(Score.of(6, 4), 1, 1),
                    new ScoreFrames(Score.of(0, 0), 2, 4),
                    new ScoreFrames(Score.of(0, 1), 5, 8),
                    new ScoreFrames(Score.of(0, 2), 9, 12)
            );
            assertIterableEquals(expected, reducedScores);
        }
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