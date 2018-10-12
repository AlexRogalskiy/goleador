package ris58h.goleador.core;

import ris58h.goleador.core.processor.*;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MainProcessor {
    private static final Map<String, Supplier<Processor>> SUPPLIERS = new HashMap<>();
    static {
        SUPPLIERS.put("frame", FramesProcessor::new);
        SUPPLIERS.put("static", StaticProcessor::new);
        SUPPLIERS.put("blur", BlurProcessor::new);
        SUPPLIERS.put("tesseractExternal", TesseractExternalProcessor::new);
        SUPPLIERS.put("scores", ScoresProcessor::new);
        SUPPLIERS.put("reduceScores", ReduceScoresProcessor::new);
    }

    private static final ProcessorFactory PROCESSOR_FACTORY = type -> SUPPLIERS.get(type).get();

    private final Pipeline pipeline = Pipeline.create(String.join(",", Arrays.asList(
            "frame",
            "static",
            "blur",
            "ocr:tesseractExternal",
            "scores",
            "reduceScores"
    )), PROCESSOR_FACTORY);

    public void init() throws Exception {
        this.init(Parameters.empty());
    }

    public void init(Parameters overrideParameters) throws Exception {
        InputStream is = MainProcessor.class.getResourceAsStream("main-pipeline.properties");
        Parameters mainParameters = Parameters.fromInputStream(is);
        pipeline.init(Parameters.combine(overrideParameters, mainParameters));
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
