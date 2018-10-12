package ris58h.goleador.core;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Disabled
class MainProcessorIT {
    static MainProcessor mainProcessor = new MainProcessor();

    @BeforeAll
    static void beforeAll() throws Exception {
        mainProcessor.init();
    }

    @AfterAll
    static void afterAll() throws Exception {
        mainProcessor.dispose();
    }

    @TestFactory
    Stream<DynamicTest> tests() {
        return MainProcessorTestData.DATA_BY_VIDEO.entrySet().stream()
                .map(e -> DynamicTest.dynamicTest(e.getKey(), () -> {
                    test(e.getKey(), e.getValue());
                }));
    }

    private void test(String videoId, List<String> expectedLines) throws Exception {
        List<ScoreFrames> reducedScores = MainProcessorTestHelper.process(videoId, mainProcessor);
        List<String> lines = reducedScores.stream()
                .map(ScoreFrames::toString)
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(expectedLines, lines, () -> {
            String expected = String.join("\n", expectedLines);
            String actual = String.join("\n", lines);
            return "\nExpected:\n" + expected + "\nActual:\n" + actual;
        });
    }
}
