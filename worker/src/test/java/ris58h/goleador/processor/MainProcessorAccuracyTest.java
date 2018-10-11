package ris58h.goleador.processor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainProcessorAccuracyTest {
    static final String FORMAT = "136"; // 720p

    static final Path testDirPath = Paths.get("test");

    public static void main(String[] args) throws Exception {
        MainProcessor mainProcessor = new MainProcessor();
        mainProcessor.init();
        AccuracyTest.runFor(Collections.singletonList(
                Collections.singletonList(1)//TODO
        ), params -> measure(mainProcessor));
        mainProcessor.dispose();
    }

    private static double measure(MainProcessor mainProcessor) {
        F1ScoreMeasure<Score> measure = new F1ScoreMeasure<>();
        for (Map.Entry<String, List<String>> entry : MainProcessorTestData.DATA_BY_VIDEO.entrySet()) {
            try {
                process(entry.getKey(), entry.getValue(), measure, mainProcessor);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return measure.result();
    }

    private static void process(String videoId,
                                List<String> expectedLines,
                                F1ScoreMeasure<Score> measure,
                                MainProcessor mainProcessor) throws Exception {
        Path inputPath = testDirPath.resolve("video").resolve(FORMAT).resolve(videoId + ".mp4");
        File inputFile = inputPath.toFile();
        if (!inputFile.exists()) {
            throw new RuntimeException("Input file not found: " + inputFile);
        }
        String input = inputFile.getAbsolutePath();
        Path workingDirPath = testDirPath.resolve("work").resolve(FORMAT).resolve(videoId);
        File workingDirFile = workingDirPath.toFile();
        if (!workingDirFile.exists()) {
            workingDirFile.mkdirs();
        }
        String workingDir = workingDirFile.getAbsolutePath();
        List<ScoreFrames> reducedScores = mainProcessor.process(input, workingDir);
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
