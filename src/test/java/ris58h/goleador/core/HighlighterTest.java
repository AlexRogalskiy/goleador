package ris58h.goleador.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HighlighterTest {
    @Test
    void test__qGLWEaa47k() throws Exception {
        test(Arrays.asList(
                "0-0:34:79",
                "1-0:80:115",
                "1-1:116:331",
                "2-1:332:370",
                "2-2:371:442",
                "2-3:443:506"
        ), Arrays.asList(
                64,
                97,
                315,
                360,
                427
        ));
    }

    @Test
    void test_5VMS71fitI4() throws Exception {
        test(Arrays.asList(
                "0-0:47:151",
                "0-1:152:256",
                "1-1:257:375"
        ), Arrays.asList(
                139,
                243
        ));
    }

    @Test
    void test_cLjn6oF1E9Q() throws Exception {
        test(Arrays.asList(
                "0-0:1:29",
                "0-1:30:146",
                "0-2:147:235",
                "1-2:236:282"
        ), Arrays.asList(
                14,
                133,
                224
        ));
    }

    @Test
    void test_D6hdF7gChmE() throws Exception {
        test(Arrays.asList(
                "0-0:1:209",
                "0-1:214:256",
                "1-1:261:330",
                "2-1:335:405",
                "3-1:411:478"
        ), Arrays.asList(
                200,
                255,
                319,
                393
        ));
    }

    @Test
    void test_fM7TtiC_j_w() throws Exception {
        test(Arrays.asList(
                "0-0:1:29",
                "1-0:34:87",
                "1-1:92:151",
                "2-1:156:236",
                "3-1:241:282"
        ), Arrays.asList(
                10,
                75,
                135,
                216
        ));
    }

    @Test
    void test_gLQf3Zp2n6g() throws Exception {
        test(Arrays.asList(
                "0-0:1:19",
                "1-0:99:106",
                "2-0:151:175",
                "3-0:260:275"
        ), Arrays.asList(
                10,
                105,
                170
        ));
    }

    @Test
    void test_KyW4keXAT3s() throws Exception {
        test(Arrays.asList(
                "0-0:44:218",
                "1-0:219:245",
                "1-1:249:366"
        ), Arrays.asList(
                208,
                235
        ));
    }

    @Test
    void test_QYlSNDwrq40() throws Exception {
        test(Arrays.asList(
                "0-0:1:23",
                "1-0:28:102",
                "1-1:107:242"
        ), Arrays.asList(
                13,
                93
        ));
    }

    @Test
    void test_ZdFEZlepWJI() throws Exception {
        test(Arrays.asList(
                "0-0:1:145",
                "0-1:146:390",
                "1-1:391:479",
                "2-1:480:546"
        ), Arrays.asList(
                127,
                377,
                465
        ));
    }

    private static void test(List<String> reducedScoreLines, List<Integer> expectedTimes) {
        List<ScoreFrames> reducedScores = reducedScoreLines.stream()
                .map(ScoreFrames::parseScoreRange)
                .collect(Collectors.toList());
        List<Integer> times = Highlighter.times(reducedScores);
        assertIterableEquals(expectedTimes, times);
    }
}