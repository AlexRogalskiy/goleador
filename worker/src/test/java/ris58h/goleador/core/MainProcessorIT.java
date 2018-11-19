package ris58h.goleador.core;

import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Disabled
class MainProcessorIT {
    private static MainProcessor mainProcessor = new MainProcessor();
    private static HashSet<String> videos = new HashSet<>();

    @BeforeAll
    static void beforeAll() throws Exception {
        mainProcessor.init();
        videos.addAll(ReducedScoresTestData.DATA_BY_VIDEO.keySet());
    }

    @AfterAll
    static void afterAll() throws Exception {
        if (!videos.isEmpty()) {
            System.out.println("WARNING: some videos are unused " + videos);
        }
        mainProcessor.dispose();
    }

    //TODO monochrome threshold problem
    @Test
    void test__qGLWEaa47k() throws Exception {
        test("-qGLWEaa47k");
    }

    @Test
    void test_5VMS71fitI4() throws Exception {
        test("5VMS71fitI4");
    }

    @Test
    void test_cLjn6oF1E9Q() throws Exception {
        test("cLjn6oF1E9Q");
    }

    //TODO OCR problem: 0-1 recognized as 0-2 (105:261)
    @Test
    void test_C9hwnys6qXM() throws Exception {
        test("C9hwnys6qXM");
    }

    @Test
    void test_D6hdF7gChmE() throws Exception {
        test("D6hdF7gChmE");
    }
    
    @Test
    void test_fM7TtiC_j_w() throws Exception {
        test("fM7TtiC-j_w");
    }

    @Test
    void test_gLQf3Zp2n6g() throws Exception {
        test("gLQf3Zp2n6g");
    }

    @Test
    void test_KyW4keXAT3s() throws Exception {
        test("KyW4keXAT3s");
    }

    @Test
    void test_ncFTbp_UNk4() throws Exception {
        test("ncFTbp_UNk4");
    }

    //TODO static pixels recognition problem: video is too short
    @Test
    void test_PKzvJgRx1Zw() throws Exception {
        test("PKzvJgRx1Zw");
    }

    @Test
    void test_QYlSNDwrq40() throws Exception {
        test("QYlSNDwrq40");
    }

    @Test
    void test_yE33DcpNZkw() throws Exception {
        test("yE33DcpNZkw");
    }

    @Test
    void test_Xf5z_awHVKw() throws Exception {
        test("Xf5z_awHVKw");
    }

    @Test
    void test_XL1kNQ_HRAE() throws Exception {
        test("XL1kNQ_HRAE");
    }

    @Test
    void test_ZdFEZlepWJI() throws Exception {
        test("ZdFEZlepWJI");
    }

    private void test(String videoId) throws Exception {
        List<String> expectedLines = ReducedScoresTestData.DATA_BY_VIDEO.get(videoId);
        videos.remove(videoId);
        test(videoId, expectedLines);
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
