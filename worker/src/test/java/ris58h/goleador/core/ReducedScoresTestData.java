package ris58h.goleador.core;

import java.util.*;

class ReducedScoresTestData {
    static final Map<String, List<String>> DATA_BY_VIDEO = new TreeMap<>();

    static {
        //TODO monochrome threshold problem
        addTestData("-qGLWEaa47k", Arrays.asList(
                "0-0:34:79",
                "1-0:80:115",
                "1-1:116:188",
                "1-1:196:211",
                "1-1:214:331",
                "2-1:332:370",
                "2-2:371:395",
                "2-2:401:442",
                "2-3:443:468",
                "2-3:474:506"
        ));
        addTestData("5VMS71fitI4", Arrays.asList(
                "0-0:47:47",
                "0-0:51:59",
                "0-0:65:72",
                "0-0:79:91",
                "0-0:103:106",
                "0-0:110:118",
                "0-0:123:129",
                "0-0:140:151",
                "0-1:152:160",
                "0-1:185:185",
                "0-1:187:194",
                "0-1:213:222",
                "0-1:230:230",
                "0-1:234:240",
                "0-1:245:256",
                "1-1:257:260",
                "1-1:279:287",
                "1-1:296:304",
                "1-1:311:323",
                "1-1:330:340",
                "1-1:348:354",
                "1-1:362:375"
        ));
        addTestData("cLjn6oF1E9Q", Arrays.asList(
                "0-0:1:29",
                "0-1:30:146",
                "0-2:147:235",
                "1-2:236:282"
        ));
        //TODO OCR problem: 0-1 recognized as 0-2 (105:261)
        addTestData("C9hwnys6qXM", Arrays.asList(
                "0-0:25:40",
                "0-1:41:204",
                "0-1:219:318"
        ));
        addTestData("D6hdF7gChmE", Arrays.asList(
                "0-0:1:106",
                "0-0:114:209",
                "0-1:214:256",
                "1-1:261:308",
                "1-1:315:330",
                "2-1:335:383",
                "2-1:391:405",
                "3-1:411:467",
                "3-1:474:478"
        ));
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
        //TODO static pixels recognition problem: video is too short
        addTestData("PKzvJgRx1Zw", Arrays.asList(
                "0-0:6:19",
                "0-1:20:39",
                "0-0:40:51",
                "1:1:74:81"
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
