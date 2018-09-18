package ris58h.goleador.core;

public class MainProcessor {
    public static void process(String videoId, String dirName) throws Exception {
        FramesProcessor.process(videoId, dirName, "-monochrome");
        PreOcrProcessor.process(dirName, "-monochrome", "-preocr");
        OcrProcessor.process(dirName, "-preocr", "-text");
        ScoresProcessor.process(dirName, "-text", "timestamps.txt");
    }
}
