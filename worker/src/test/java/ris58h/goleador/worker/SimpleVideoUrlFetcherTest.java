package ris58h.goleador.worker;

import org.junit.jupiter.api.Test;
import ris58h.goleador.core.IOUtils;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleVideoUrlFetcherTest {

    @Test
    void extractFromResponse() throws Exception {
        InputStream is = SimpleVideoUrlFetcher.class.getResourceAsStream("get_video_info_DU4Uij016HQ");
        String response = IOUtils.readInputToString(is);
        String videoUrl = SimpleVideoUrlFetcher.extractFromResponse(response, "136");
        String expected = "https://r1---sn-axq7sn76.googlevideo.com/videoplayback?txp=5533432&ms=au%2Conr&itag=136&mt=1539095021&gir=yes&ip=94.19.239.167&requiressl=yes&ipbits=0&gcr=ru&mime=video%2Fmp4&id=o-AEZWFPNK2SZdysjOVxSOBENVc4T8LxA-c1Vui-V5XvNN&pl=20&mm=31%2C26&mn=sn-axq7sn76%2Csn-5goeen7y&fvip=1&keepalive=yes&mv=m&key=yt6&lmt=1538695792987922&c=WEB&signature=AB5A1728C14C77E8A3AFB4B66FD533ACB10AEDDB.BEF43CA88A145DC3CBA4302491DEAFE338ECD539&nh=IgphcjAyLmxlZDAzKgkxMjcuMC4wLjE%2C&source=youtube&clen=118273331&sparams=aitags%2Cclen%2Cdur%2Cei%2Cgcr%2Cgir%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Ckeepalive%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cnh%2Cpl%2Crequiressl%2Csource%2Cexpire&aitags=133%2C134%2C135%2C136%2C160%2C242%2C243%2C244%2C247%2C278&expire=1539116714&ei=Srq8W_3FJJCd7QSGqIGABg&initcwndbps=1288750&dur=597.320";
        assertEquals(expected, videoUrl);
    }
}