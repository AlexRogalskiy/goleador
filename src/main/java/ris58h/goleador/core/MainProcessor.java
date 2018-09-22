package ris58h.goleador.core;

public class MainProcessor {
    public static void process(String videoId, String dirName) throws Exception {
        FramesProcessor.process(videoId, dirName, "-gray");
        PreOcrProcessor.process(dirName, "-gray", "-preocr");
        OcrProcessor.process(dirName, "-preocr", "-text");
        ScoresProcessor.process(dirName, "-text", "-score");
        TimestampsProcessor.process(dirName, "-score", "timestamps.txt");
    }
}
