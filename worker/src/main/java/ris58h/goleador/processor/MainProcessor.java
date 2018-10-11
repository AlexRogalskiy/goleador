package ris58h.goleador.processor;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainProcessor {
    private final Pipeline pipeline = Pipeline.create(String.join(",", Arrays.asList(
            "frame",
            "static",
            "preocr",
            "ocr",
            "scores",
            "reduceScores"
    )), DefaultProcessorFactory.INSTANCE);

    public void init() throws Exception {
        InputStream is = MainProcessor.class.getResourceAsStream("main-pipeline.properties");
        pipeline.init(Props.fromInputStream(is));
    }

    public List<ScoreFrames> process(String input, String dirName) throws Exception {
        pipeline.process(input, "reduced-scores", dirName);
        return Files.lines(Paths.get(dirName).resolve("reduced-scores.txt"))
                .map(ScoreFrames::parse)
                .collect(Collectors.toList());
    }

    public void dispose() throws Exception {
        pipeline.dispose();
    }
}
