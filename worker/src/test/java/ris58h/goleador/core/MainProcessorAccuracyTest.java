package ris58h.goleador.core;

import ris58h.goleador.core.processor.Parameters;

import java.util.*;

public class MainProcessorAccuracyTest {

    public static void main(String[] args) throws Exception {
        AccuracyTest.runFor(Collections.singletonList(
                Collections.singletonList(3) // psm
        ), params -> {
            try {
                MainProcessor mainProcessor = new MainProcessor();
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put("ocr.psm", Integer.toString((Integer) params.get(0)));
                mainProcessor.init(Parameters.fromMap(paramsMap));
                double measure = measure(mainProcessor);
                mainProcessor.dispose();
                return measure;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, true);
    }

    private static double measure(MainProcessor mainProcessor) {
        ConfusionMatrix<Score> measure = new ConfusionMatrix<>();
        for (Map.Entry<String, List<String>> entry : MainProcessorTestData.DATA_BY_VIDEO.entrySet()) {
            try {
                process(entry.getKey(), entry.getValue(), measure, mainProcessor);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return measure.f1();
    }

    private static void process(String videoId,
                                List<String> expectedLines,
                                ConfusionMatrix<Score> measure,
                                MainProcessor mainProcessor) throws Exception {
        List<ScoreFrames> reducedScores = MainProcessorTestHelper.process(videoId, mainProcessor);
        Map<Integer, Score> expectedScoreByFrame = scoreByFrame(expectedLines.stream()
                .map(ScoreFrames::parse)
                .iterator());
        for (ScoreFrames actualScoreFrame : reducedScores) {
            Score actualScore = actualScoreFrame.score;
            for (int i = actualScoreFrame.first; i <= actualScoreFrame.last; i++) {
                Score expectedScore = expectedScoreByFrame.remove(i);
                measure.add(expectedScore, actualScore);
            }
        }
        for (Score expectedScore : expectedScoreByFrame.values()) {
            measure.add(expectedScore, null);
        }
    }

    private static Map<Integer, Score> scoreByFrame(Iterator<ScoreFrames> iterator) {
        Map<Integer, Score> result = new HashMap<>();
        while (iterator.hasNext()) {
            ScoreFrames scoreFrames = iterator.next();
            for (int i = scoreFrames.first; i <= scoreFrames.last; i++) {
                result.put(i, scoreFrames.score);
            }
        }
        return result;
    }
}
