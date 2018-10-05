package ris58h.goleador.processor;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainProcessor {
    public static List<ScoreFrames> process(String input, String dirName) throws Exception {
        FramesProcessor.process(dirName, input, "-gray");
        ClearProcessor.process(dirName, "-gray", "-clear");
        PreOcrProcessor.process(dirName, "-clear", "-preocr");
        OcrProcessor.process(dirName, "-preocr", "-text");
        ScoresProcessor.process(dirName, "-text", "-score");
        ReduceScoresProcessor.process(dirName, "-score", "reduced-scores.txt");
        return Files.lines(Paths.get(dirName).resolve("reduced-scores.txt"))
                .map(ScoreFrames::parseScoreRange)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws Exception {
        String input = args[0];
        String output = args[1];
        Path tempDirectory = Files.createTempDirectory("ygp-");
        try {
            String dirName = tempDirectory.toAbsolutePath().toString();
            System.out.println("Working dir: " + dirName);
            List<ScoreFrames> scoreFrames = process(input, dirName);
            List<Integer> times = Highlighter.times(scoreFrames);
            List<String> lines = times.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            Files.write(Paths.get(output), lines);
        } finally {
            Utils.deleteDirectory(tempDirectory);
        }
    }
}
