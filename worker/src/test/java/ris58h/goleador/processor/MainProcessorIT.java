package ris58h.goleador.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainProcessorIT {
    static final String FORMAT = "136"; // 720p

    Path testDirPath = Paths.get("test");

    //TODO
    @Test
    void test__qGLWEaa47k() throws Exception {
        test("-qGLWEaa47k", Arrays.asList(
                "0-0:34:79",
                "1-0:80:115",
                "1-1:116:331",
                "2-1:332:370",
                "2-2:371:442",
                "2-3:443:506"
        ));
    }

    @Test
    void test_5VMS71fitI4() throws Exception {
        test("5VMS71fitI4", Arrays.asList(
                "0-0:47:151",
                "0-1:152:256",
                "1-1:257:375"
        ));
    }

    @Test
    void test_cLjn6oF1E9Q() throws Exception {
        test("cLjn6oF1E9Q", Arrays.asList(
                "0-0:1:29",
                "0-1:30:146",
                "0-2:147:235",
                "1-2:236:282"
        ));
    }

    @Test
    void test_D6hdF7gChmE() throws Exception {
        test("D6hdF7gChmE", Arrays.asList(
                "0-0:1:209",
                "0-1:214:256",
                "1-1:261:330",
                "2-1:335:405",
                "3-1:411:478"
        ));
    }

    @Test
    void test_fM7TtiC_j_w() throws Exception {
        test("fM7TtiC-j_w", Arrays.asList(
                "0-0:1:29",
                "1-0:34:87",
                "1-1:92:151",
                "2-1:156:236",
                "3-1:241:282"
        ));
    }

    @Test
    void test_gLQf3Zp2n6g() throws Exception {
        test("gLQf3Zp2n6g", Arrays.asList(
                "0-0:1:19",
                "1-0:99:106",
                "2-0:151:175",
                "3-0:260:275"
        ));
    }

    //TODO
    @Test
    void test_KyW4keXAT3s() throws Exception {
        test("KyW4keXAT3s", Arrays.asList(
                "0-0:44:218",
                "1-0:219:245",
                "1-1:249:366"
        ));
    }

    @Test
    void test_QYlSNDwrq40() throws Exception {
        test("QYlSNDwrq40", Arrays.asList(
                "0-0:1:23",
                "1-0:28:102",
                "1-1:107:242"
        ));
    }

    //TODO: Actual   :<1-2:104:111>
    @Test
    void test_yE33DcpNZkw() throws Exception {
        test("yE33DcpNZkw", Arrays.asList(
                "0-0:7:16",
                "0-1:17:60",
                "1-1:61:82",
                "1-2:104:112",
                "2-2:113:138",
                "3-2:139:181",
                "3-3:182:186"
        ));
    }

    @Test
    void test_ZdFEZlepWJI() throws Exception {
        test("ZdFEZlepWJI", Arrays.asList(
                "0-0:1:145",
                "0-1:146:390",
                "1-1:391:479",
                "2-1:480:546"
        ));
    }

    void test(String videoId, List<String> expectedLines) throws Exception {
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
        List<String> lines = MainProcessor.process(input, workingDir).stream()
                .map(ScoreFrames::toString)
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(expectedLines, lines);
    }
}
