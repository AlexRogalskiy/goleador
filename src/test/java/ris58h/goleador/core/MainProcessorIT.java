package ris58h.goleador.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MainProcessorIT {
    static final String FORMAT = "136"; // 720p

    Path testDirPath = Paths.get("test");

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
    void test_gLQf3Zp2n6g() throws Exception {
        test("gLQf3Zp2n6g", Arrays.asList(
                "0-0:1:19",
                "1-0:99:106",
                "2-0:151:175",
                "3-0:260:275"
        ));
    }

    @Test
    void test_KyW4keXAT3s() throws Exception {
        test("KyW4keXAT3s", Arrays.asList(
                "0-0:44:218",
                "1-0:219:245",
                "1-1:249:366"
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
        MainProcessor.process(input, workingDir);
        List<String> lines = Files.readAllLines(workingDirPath.resolve("reduced-scores.txt"));
        Assertions.assertIterableEquals(expectedLines, lines);
    }
}
