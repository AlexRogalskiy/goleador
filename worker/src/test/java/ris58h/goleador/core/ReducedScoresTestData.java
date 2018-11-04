package ris58h.goleador.core;

import java.util.*;

class ReducedScoresTestData {
    static final Map<String, List<String>> DATA_BY_VIDEO = new TreeMap<>();

    static {
        addTestData("fM7TtiC-j_w", Arrays.asList(
                "0-0:1:29",
                "1-0:34:87",
                "1-1:92:151",
                "2-1:156:210",
                "2-1:215:236",
                "3-1:241:282"
        ));
        addTestData("gLQf3Zp2n6g", Arrays.asList(
                "0-0:1:19",
                "1-0:99:106",
                "2-0:151:175",
                "3-0:260:275"
        ));
        addTestData("KyW4keXAT3s", Arrays.asList(
                "0-0:44:143",
                "0-0:145:180",
                "0-0:196:218",
                "1-0:219:245",
                "1-1:249:324",
                "1-1:327:366"
        ));
        addTestData("PKzvJgRx1Zw", Arrays.asList(
                "0-0:6:19",
                "0-1:20:39",
                "0-0:40:51",
                "1-1:74:81"
        ));
        addTestData("QYlSNDwrq40", Arrays.asList(
                "0-0:1:23",
                "1-0:28:78",
                "1-0:83:102",
                "1-1:107:109",
                "1-1:201:242"
        ));
        addTestData("yE33DcpNZkw", Arrays.asList(
                "0-0:7:16",
                "0-1:17:18",
                "0-1:33:45",
                "0-1:57:60",
                "1-1:61:61",
                "1-1:68:82",
                "1-2:104:112",
                "2-2:113:116",
                "2-2:129:138",
                "3-2:139:139",
                "3-2:156:167",
                "3-2:179:181",
                "3-3:182:186"
        ));
        addTestData("Xf5z_awHVKw", Arrays.asList(
                "0-0:55:75",
                "0-0:101:118",
                "0-1:148:154",
                "0-1:160:173",
                "1-1:194:206",
                "1-1:223:228",
                "1-1:251:262",
                "1-1:275:282",
                "1-1:299:309",
                "2-1:335:351",
                "2-1:367:377",
                "2-2:400:410"
        ));
        addTestData("XL1kNQ_HRAE", Arrays.asList(
                "0-0:46:135",
                "1-0:141:172",
                "1-0:199:306"
        ));
        addTestData("ZdFEZlepWJI", Arrays.asList(
                "0-0:1:70",
                "0-0:77:108",
                "0-0:127:145",
                "0-1:146:160",
                "0-1:201:228",
                "0-1:236:260",
                "0-1:273:299",
                "0-1:312:359",
                "0-1:369:390",
                "1-1:391:412",
                "1-1:448:452",
                "1-1:459:479",
                "2-1:480:514",
                "2-1:544:546"
        ));
    }

    private static void addTestData(String videoId, List<String> expectedLines) {
        DATA_BY_VIDEO.put(videoId, expectedLines);
    }
}
