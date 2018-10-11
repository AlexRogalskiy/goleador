package ris58h.goleador.worker;

import java.util.concurrent.TimeUnit;

import static ris58h.goleador.core.IOUtils.readInputToString;

public class YoutubeDLVideoUrlFetcher {

    public static String fetchFor(String videoId, String format) throws Exception {
        String command = "youtube-dl -f " + format + " -g https://www.youtube.com/watch?v=" + videoId;
        Process process = Runtime.getRuntime().exec(command);
        if (!process.waitFor(1, TimeUnit.MINUTES)) {
            throw new RuntimeException("Process timeout");
        }
        int exitCode = process.exitValue();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        }
        return readInputToString(process.getInputStream());
    }
}
