package ris58h.goleador.core;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighlighterTest {

    @Test
    void shouldDoNothingIfThereIsNothingToDo() {
        List<ScoreFrames> scores = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 10),
                new ScoreFrames(Score.of(0, 1), 11, 20)
        );
        List<ScoreFrames> reducedScores = Highlighter.prepare(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 10),
                new ScoreFrames(Score.of(0, 1), 11, 20)
        );
        assertIterableEquals(expected, reducedScores);
    }

    @Test
    void test1() {
        List<ScoreFrames> scores = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 4, 4),
                new ScoreFrames(Score.of(0, 4), 5, 5),
                new ScoreFrames(Score.of(0, 0), 6, 6),
                new ScoreFrames(Score.of(0, 1), 10, 12),
                new ScoreFrames(Score.of(1, 1), 16, 18),
                new ScoreFrames(Score.of(2, 6), 19, 19)
        );
        List<ScoreFrames> reducedScores = Highlighter.prepare(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 4, 6),
                new ScoreFrames(Score.of(0, 1), 10, 12),
                new ScoreFrames(Score.of(1, 1), 16, 18)
        );
        assertIterableEquals(expected, reducedScores);
    }

    @Test
    void test2() {
        List<ScoreFrames> scores = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 2),
                new ScoreFrames(Score.of(0, 1), 3, 3),
                new ScoreFrames(Score.of(0, 0), 4, 6),
                new ScoreFrames(Score.of(1, 0), 7, 7)
        );
        List<ScoreFrames> reducedScores = Highlighter.prepare(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 6)
        );
        assertIterableEquals(expected, reducedScores);
    }

    @Test
    void test3() {
        List<ScoreFrames> scores = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 3),
                new ScoreFrames(Score.of(0, 1), 4, 6),
                new ScoreFrames(Score.of(0, 0), 7, 7),
                new ScoreFrames(Score.of(0, 2), 8, 10)
        );
        List<ScoreFrames> reducedScores = Highlighter.prepare(scores);
        List<ScoreFrames> expected = Arrays.asList(
                new ScoreFrames(Score.of(0, 0), 1, 3),
                new ScoreFrames(Score.of(0, 1), 4, 6),
                new ScoreFrames(Score.of(0, 2), 8, 10)
        );
        assertIterableEquals(expected, reducedScores);
    }

    @Nested
    class ScoresThatDontStartWith_0_0 {
        @Test
        void test1() {
            List<ScoreFrames> scores = Arrays.asList(
                    new ScoreFrames(Score.of(0, 1), 1, 1)
            );
            List<ScoreFrames> reducedScores = Highlighter.prepare(scores);
            assertTrue(reducedScores.isEmpty());
        }

        @Test
        void test2() {
            List<ScoreFrames> scores = Arrays.asList(
                    new ScoreFrames(Score.of(1, 1), 1, 1)
            );
            List<ScoreFrames> reducedScores = Highlighter.prepare(scores);
            assertTrue(reducedScores.isEmpty());
        }

        @Test
        void test3() {
            List<ScoreFrames> scores = Arrays.asList(
                    new ScoreFrames(Score.of(6, 4), 1, 1),
                    new ScoreFrames(Score.of(0, 0), 2, 4),
                    new ScoreFrames(Score.of(0, 1), 5, 8),
                    new ScoreFrames(Score.of(0, 2), 9, 12)
            );
            List<ScoreFrames> reducedScores = Highlighter.prepare(scores);
            List<ScoreFrames> expected = Arrays.asList(
                    new ScoreFrames(Score.of(0, 0), 2, 4),
                    new ScoreFrames(Score.of(0, 1), 5, 8),
                    new ScoreFrames(Score.of(0, 2), 9, 12)
            );
            assertIterableEquals(expected, reducedScores);
        }
    }
}