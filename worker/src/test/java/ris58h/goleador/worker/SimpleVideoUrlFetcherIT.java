package ris58h.goleador.worker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleVideoUrlFetcherIT {
    @Test
    void test() throws Exception {
        String videoUrl = SimpleVideoUrlFetcher.fetchFor("_Sd119oRamE", "136");
        String expected = "https://r5---sn-gxuog0-axqe.googlevideo.com/videoplayback?gcr=ru&fvip=3&gir=yes&signature=BFCA3838E73CC100C23662709BEE83D88D4E6AC2.9CD8BB96EDD60DA0199C1E959D623B4FCC1D7F1F&ip=94.19.239.167&requiressl=yes&aitags=133%2C134%2C135%2C136%2C160%2C242%2C243%2C244%2C247%2C278&id=o-AN75JVqXQqWDGLKxkKC_yBpFjZnb-AB--BSv9wnFaUj2&c=WEB&txp=5533432&ei=QZ-_W--GE42XyQXAp43oAw&nh=%2CIgphcjAyLmxlZDAzKgkxMjcuMC4wLjE&lmt=1539210285363913&source=youtube&dur=400.400&key=yt6&mime=video%2Fmp4&ipbits=0&pl=20&clen=69233797&expire=1539306401&keepalive=yes&mm=31%2C29&mn=sn-gxuog0-axqe%2Csn-axq7sn76&initcwndbps=986250&itag=136&ms=au%2Crdu&mt=1539284624&mv=m&sparams=aitags%2Cclen%2Cdur%2Cei%2Cgcr%2Cgir%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Ckeepalive%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cnh%2Cpl%2Crequiressl%2Csource%2Cexpire&ratebypass=yes";
        assertEquals(expected, videoUrl);
    }
}
