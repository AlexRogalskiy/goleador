package ris58h.goleador.core;

public class MainProcessor {
    public static void process(String input, String dirName) throws Exception {
        FramesProcessor.process(dirName, input, "-gray");
        ClearProcessor.process(dirName, "-gray", "-clear");
        PreOcrProcessor.process(dirName, "-clear", "-preocr");
        OcrProcessor.process(dirName, "-preocr", "-text");
        ScoresProcessor.process(dirName, "-text", "-score");
        ReduceScoresProcessor.process(dirName, "-score", "reduced-scores.txt");
    }
}
