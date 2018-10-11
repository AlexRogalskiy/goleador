package ris58h.goleador.core;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainProcessorTestHelper {
    static final String FORMAT = "136"; // 720p

    static final Path testDirPath = Paths.get("test");

    public static List<ScoreFrames> process(String videoId, MainProcessor mainProcessor) throws Exception {
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
        return mainProcessor.process(input, workingDir);
    }

    // Just the way to process a particular video.
    public static void main(String[] args) throws Exception {
        String videoId = "_Sd119oRamE";
        MainProcessor mainProcessor = new MainProcessor();
        mainProcessor.init();
        List<ScoreFrames> reducedScores = MainProcessorTestHelper.process(videoId, mainProcessor);
        reducedScores.forEach(System.out::println);
        mainProcessor.dispose();
    }
}
