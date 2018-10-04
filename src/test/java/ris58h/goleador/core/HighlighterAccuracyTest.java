package ris58h.goleador.core;

import ris58h.goleador.core.HighlighterTestData.ExpectedTime;
import ris58h.goleador.core.HighlighterTestData.ScoresAndTimes;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HighlighterAccuracyTest {
    static int back; // Yep, it's a global variable!

    public static void main(String[] args) {
        for (back = 0; back < 30; back++) {
            Number number = measure();
            System.out.println(String.format(
                    "%s for back=%d",
                    number.toString(),
                    back
            ));
        }
    }

    public static Number measure() {
        LeastSquaresMeasure measure = new LeastSquaresMeasure();
        for (Map.Entry<String, ScoresAndTimes> entry : HighlighterTestData.DATA_BY_VIDEO.entrySet()) {
            ScoresAndTimes value = entry.getValue();
            process(value.reducedScoreLines, value.expectedTimes, measure);
        }
        return measure.processResult();
    }

    private static void process(List<String> reducedScoreLines,
                                List<ExpectedTime> expectedTimes,
                                LeastSquaresMeasure measure) {
        List<ScoreFrames> reducedScores = reducedScoreLines.stream()
                .map(ScoreFrames::parseScoreRange)
                .collect(Collectors.toList());
        List<Integer> times = Highlighter.times(reducedScores, back);
        Iterator<ExpectedTime> expectedIterator = expectedTimes.iterator();
        Iterator<Integer> iterator = times.iterator();
        while (expectedIterator.hasNext()) {
            ExpectedTime expected = expectedIterator.next();
            Integer actual = iterator.next();
            measure.process(expected, actual);
        }
        if (iterator.hasNext()) {
            throw new RuntimeException("Size doesn't match");
        }
    }

    private static class LeastSquaresMeasure {
        int sum = 0;

        void process(ExpectedTime expected, int actual) {
            int residual;
            if (actual < expected.earliest) {
                residual = expected.earliest - actual;
            } else if (actual > expected.latest) {
                residual = actual - expected.latest;
            } else {
                residual = 0;
            }
            this.sum += residual * residual;
        }

        Number processResult() {
            return this.sum;
        }
    }
}
