package ris58h.goleador.processor;

import java.util.*;

class MainProcessorTestData {
    static final Map<String, List<String>> DATA_BY_VIDEO = new TreeMap<>();

    static {
        //TODO
        addTestData("-qGLWEaa47k", Arrays.asList(
                "0-0:34:79",
                "1-0:80:115",
                "1-1:116:331",
                "2-1:332:370",
                "2-2:371:442",
                "2-3:443:506"
        ));
        addTestData("5VMS71fitI4", Arrays.asList(
                "0-0:47:151",
                "0-1:152:256",
                "1-1:257:375"
        ));
        addTestData("cLjn6oF1E9Q", Arrays.asList(
                "0-0:1:29",
                "0-1:30:146",
                "0-2:147:235",
                "1-2:236:282"
        ));
        addTestData("D6hdF7gChmE", Arrays.asList(
                "0-0:1:209",
                "0-1:214:256",
                "1-1:261:330",
                "2-1:335:405",
                "3-1:411:478"
        ));
        addTestData("fM7TtiC-j_w", Arrays.asList(
                "0-0:1:29",
                "1-0:34:87",
                "1-1:92:151",
                "2-1:156:236",
                "3-1:241:282"
        ));
        addTestData("gLQf3Zp2n6g", Arrays.asList(
                "0-0:1:19",
                "1-0:99:106",
                "2-0:151:175",
                "3-0:260:275"
        ));
        //TODO
        addTestData("KyW4keXAT3s", Arrays.asList(
                "0-0:44:218",
                "1-0:219:245",
                "1-1:249:366"
        ));
        addTestData("QYlSNDwrq40", Arrays.asList(
                "0-0:1:23",
                "1-0:28:102",
                "1-1:107:242"
        ));
        //TODO: Actual   :<1-2:104:111>
        addTestData("yE33DcpNZkw", Arrays.asList(
                "0-0:7:16",
                "0-1:17:60",
                "1-1:61:82",
                "1-2:104:112",
                "2-2:113:138",
                "3-2:139:181",
                "3-3:182:186"
        ));
        addTestData("ZdFEZlepWJI", Arrays.asList(
                "0-0:1:145",
                "0-1:146:390",
                "1-1:391:479",
                "2-1:480:546"
        ));
    }

    private static void addTestData(String videoId, List<String> expectedLines) {
        DATA_BY_VIDEO.put(videoId, expectedLines);
    }
}
