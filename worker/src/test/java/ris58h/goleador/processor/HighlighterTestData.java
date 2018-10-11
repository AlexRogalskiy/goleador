package ris58h.goleador.processor;

import java.util.*;

class HighlighterTestData {
    static final Map<String, ScoresAndTimes> DATA_BY_VIDEO = new TreeMap<>();

    static {
        addTestData("-qGLWEaa47k",
                Arrays.asList(
                        "0-0:34:79",
                        "1-0:80:115",
                        "1-1:116:331",
                        "2-1:332:370",
                        "2-2:371:442",
                        "2-3:443:506"
                ), Arrays.asList(
                        new ExpectedTime(64, 67),
                        new ExpectedTime(97, 104),
                        new ExpectedTime(313, 321),
                        new ExpectedTime(360, 361),
                        new ExpectedTime(427, 434)
                ));
        addTestData("5VMS71fitI4", Arrays.asList(
                "0-0:47:151",
                "0-1:152:256",
                "1-1:257:375"
        ), Arrays.asList(
                new ExpectedTime(138, 141),
                new ExpectedTime(242, 247)
        ));
        addTestData("cLjn6oF1E9Q", Arrays.asList(
                "0-0:1:29",
                "0-1:30:146",
                "0-2:147:235",
                "1-2:236:282"
        ), Arrays.asList(
                new ExpectedTime(5, 15),
                new ExpectedTime(128, 131),
                new ExpectedTime(221, 224)
        ));
        addTestData("D6hdF7gChmE", Arrays.asList(
                "0-0:1:209",
                "0-1:214:256",
                "1-1:261:330",
                "2-1:335:405",
                "3-1:411:478"
        ), Arrays.asList(
                new ExpectedTime(200, 201),
                new ExpectedTime(244, 246),
                new ExpectedTime(317, 320),
                new ExpectedTime(390, 394)
        ));
        addTestData("fM7TtiC-j_w", Arrays.asList(
                "0-0:1:29",
                "1-0:34:87",
                "1-1:92:151",
                "2-1:156:236",
                "3-1:241:282"
        ), Arrays.asList(
                new ExpectedTime(3, 10),
                new ExpectedTime(71, 75),
                new ExpectedTime(133, 137),
                new ExpectedTime(212, 216)
        ));
        addTestData("gLQf3Zp2n6g", Arrays.asList(
                "0-0:1:19",
                "1-0:99:106",
                "2-0:151:175",
                "3-0:260:275"
        ), Arrays.asList(
                new ExpectedTime(7, 10),
                new ExpectedTime(104, 105),// There is a bad video replay that starts unexpectedly
                new ExpectedTime(165, 170)
        ));
        addTestData("KyW4keXAT3s", Arrays.asList(
                "0-0:44:218",
                "1-0:219:245",
                "1-1:249:366"
        ), Arrays.asList(
                new ExpectedTime(207, 208),
                new ExpectedTime(233, 235)
        ));
        addTestData("QYlSNDwrq40", Arrays.asList(
                "0-0:1:23",
                "1-0:28:102",
                "1-1:107:242"
        ), Arrays.asList(
                new ExpectedTime(8, 14),
                new ExpectedTime(90, 93)
        ));
        addTestData("ZdFEZlepWJI", Arrays.asList(
                "0-0:1:145",
                "0-1:146:390",
                "1-1:391:479",
                "2-1:480:546"
        ), Arrays.asList(
                new ExpectedTime(125, 129),
                new ExpectedTime(375, 378),
                new ExpectedTime(461, 465)
        ));
    }

    private static void addTestData(String videoId, List<String> reducedScoreLines, List<ExpectedTime> expectedTimes) {
        DATA_BY_VIDEO.put(videoId, new ScoresAndTimes(reducedScoreLines, expectedTimes));
    }

    static class ScoresAndTimes {
        final List<String> reducedScoreLines;
        final List<ExpectedTime> expectedTimes;

        ScoresAndTimes(List<String> reducedScoreLines, List<ExpectedTime> expectedTimes) {
            this.reducedScoreLines = reducedScoreLines;
            this.expectedTimes = expectedTimes;
        }
    }

    static class ExpectedTime {
        final int earliest;
        final int preferred;
        final int latest;

        ExpectedTime(int earliest, int preferred, int latest) {
            this.earliest = earliest;
            this.preferred = preferred;
            this.latest = latest;
        }

        ExpectedTime(int earliest, int latest) {
            this(earliest, -1, latest);
        }
    }
}
