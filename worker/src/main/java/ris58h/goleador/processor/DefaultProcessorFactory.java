package ris58h.goleador.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultProcessorFactory {
    private static final Map<String, Supplier<Processor>> SUPPLIERS = new HashMap<>();
    static {
        SUPPLIERS.put("frame", FramesProcessor::new);
        SUPPLIERS.put("static", StaticProcessor::new);
        SUPPLIERS.put("preocr", PreOcrProcessor::new);
        SUPPLIERS.put("ocr", OcrProcessor::new);
        SUPPLIERS.put("scores", ScoresProcessor::new);
        SUPPLIERS.put("reduceScores", ReduceScoresProcessor::new);
    }

    public static final ProcessorFactory INSTANCE = name -> SUPPLIERS.get(name).get();
}
