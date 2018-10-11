package ris58h.goleador.processor;

import ris58h.goleador.processor.HighlighterTestData.ExpectedTime;
import ris58h.goleador.processor.HighlighterTestData.ScoresAndTimes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class HighlighterAccuracyTest {

    public static void main(String[] args) {
        AccuracyTest.runFor(Collections.singletonList(
                IntStream.rangeClosed(0, 30).boxed().collect(Collectors.toList())
        ), params -> measure((Integer) params.get(0)));
    }

    public static Number measure(int back) {
        LeastSquaresMeasure measure = new LeastSquaresMeasure();
        for (Map.Entry<String, ScoresAndTimes> entry : HighlighterTestData.DATA_BY_VIDEO.entrySet()) {
            ScoresAndTimes value = entry.getValue();
            process(value.reducedScoreLines, value.expectedTimes, measure, back);
        }
        return measure.computeResult();
    }

    private static void process(List<String> reducedScoreLines,
                                List<ExpectedTime> expectedTimes,
                                LeastSquaresMeasure measure,
                                int back) {
        List<ScoreFrames> reducedScores = reducedScoreLines.stream()
                .map(ScoreFrames::parse)
                .collect(Collectors.toList());
        List<Integer> times = Highlighter.times(reducedScores, back);
        Iterator<ExpectedTime> expectedIterator = expectedTimes.iterator();
        Iterator<Integer> iterator = times.iterator();
        while (expectedIterator.hasNext()) {
            ExpectedTime expected = expectedIterator.next();
            Integer actual = iterator.next();
            // Little bit hacky approach
            int residual;
            if (actual < expected.earliest) {
                residual = expected.earliest - actual;
            } else if (actual > expected.latest) {
                residual = actual - expected.latest;
            } else {
                residual = 0;
            }
            measure.add(0, residual);
        }
        if (iterator.hasNext()) {
            throw new RuntimeException("Size doesn't match");
        }
    }
}
