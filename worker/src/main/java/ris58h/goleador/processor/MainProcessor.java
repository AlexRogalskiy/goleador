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
                .map(ScoreFrames::parse)
                .collect(Collectors.toList());
    }
}
