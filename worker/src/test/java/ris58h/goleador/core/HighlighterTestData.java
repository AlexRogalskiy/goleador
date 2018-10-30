package ris58h.goleador.core;

import java.util.*;

class HighlighterTestData {
    static final Map<String, ScoresAndTimes> DATA_BY_VIDEO = new TreeMap<>();

    static {
        addTestData("-qGLWEaa47k", Arrays.asList(
                new ExpectedTime(64, 67),
                new ExpectedTime(97, 104),
                new ExpectedTime(313, 321),
                new ExpectedTime(360, 361),
                new ExpectedTime(427, 434)
        ));
        addTestData("5VMS71fitI4", Arrays.asList(
                new ExpectedTime(138, 141),
                new ExpectedTime(242, 247)
        ));
        addTestData("cLjn6oF1E9Q", Arrays.asList(
                new ExpectedTime(5, 15),
                new ExpectedTime(128, 131),
                new ExpectedTime(221, 224)
        ));
        addTestData("D6hdF7gChmE", Arrays.asList(
                new ExpectedTime(200, 201),
                new ExpectedTime(244, 246),
                new ExpectedTime(317, 320),
                new ExpectedTime(390, 394)
        ));
        addTestData("fM7TtiC-j_w", Arrays.asList(
                new ExpectedTime(3, 10),
                new ExpectedTime(71, 75),
                new ExpectedTime(133, 137),
                new ExpectedTime(212, 216)
        ));
        addTestData("gLQf3Zp2n6g", Arrays.asList(
                new ExpectedTime(7, 10),
                new ExpectedTime(104, 105),// There is a bad video replay that starts unexpectedly
                new ExpectedTime(165, 170)
        ));
        addTestData("KyW4keXAT3s", Arrays.asList(
                new ExpectedTime(207, 208),
                new ExpectedTime(233, 235)
        ));
        addTestData("QYlSNDwrq40", Arrays.asList(
                new ExpectedTime(8, 14),
                new ExpectedTime(90, 93)
        ));
        addTestData("ZdFEZlepWJI", Arrays.asList(
                new ExpectedTime(125, 129),
                new ExpectedTime(375, 378),
                new ExpectedTime(461, 465)
        ));

        // Just a warning about unused data
        if (ReducedScoresTestData.DATA_BY_VIDEO.size() != DATA_BY_VIDEO.size()) {
            HashSet<String> videoIds = new HashSet<>(ReducedScoresTestData.DATA_BY_VIDEO.keySet());
            videoIds.removeAll(DATA_BY_VIDEO.keySet());
            System.out.println("WARNING: some videos are unused " + videoIds);
        }
    }

    private static void addTestData(String videoId, List<ExpectedTime> expectedTimes) {
        List<String> reducedScoreLines = ReducedScoresTestData.DATA_BY_VIDEO.get(videoId);
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
