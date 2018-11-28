package ris58h.goleador.worker;

import java.util.concurrent.TimeUnit;

import static ris58h.goleador.core.IOUtils.readInputToString;

public class YoutubeDL {
    public static String getUrl(String videoId, String format, long timeout) throws Exception {
        String command = "youtube-dl -f " + format + " -g https://www.youtube.com/watch?v=" + videoId;
        Process process = Runtime.getRuntime().exec(command);
        if (!process.waitFor(timeout, TimeUnit.MILLISECONDS)) {
            process.destroyForcibly();
            throw new RuntimeException("Process timeout");
        }
        int exitCode = process.exitValue();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        }
        return readInputToString(process.getInputStream());
    }

    public static void download(String videoId, String format, String destination, long timeout) throws Exception {
        String command = "youtube-dl -f " + format + " https://www.youtube.com/watch?v=" + videoId + " -o " + destination;
        Process process = Runtime.getRuntime().exec(command);
        if (!process.waitFor(timeout, TimeUnit.MILLISECONDS)) {
            process.destroyForcibly();
            throw new RuntimeException("Process timeout");
        }
        int exitCode = process.exitValue();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        }
    }
}
