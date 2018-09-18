package ris58h.goleador.core;

import static ris58h.goleador.core.Utils.readInputToString;

public class FramesProcessor {
    public static void process(String videoId, String dirName, String suffix) throws Exception {
//        String format = "135"; // 480p
        String format = "136"; // 720p
        System.out.println("Fetching video URL for format " + format);
        String videoUrl = fetchVideoUrl(videoId, format);
        System.out.println("Video URL for format " + format + ": " + videoUrl);

//        String width = "400";
//        String height = "100";
        String width = "500";
        String height = "150";
        String lavfiSize = width + "x" + height;
        String cropSize = width + ":" + height + ":0:0";
        String command = "ffmpeg -i " + videoUrl +
                " -f lavfi -i color=gray:size=" + lavfiSize +
                " -f lavfi -i color=black:size=" + lavfiSize +
                " -f lavfi -i color=white:size=" + lavfiSize +
                " -filter_complex [0:v]crop=" + cropSize + "[crop];[crop][1:v][2:v][3:v]threshold" +
                " -r 1 " + dirName + "/%04d" + suffix + ".png";
        System.out.println("Downloading video frames");
        long before = System.currentTimeMillis();
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        } else {
            long seconds = (System.currentTimeMillis() - before) / 1000;
            System.out.println("Frames are downloaded in " + (seconds) + "s");
        }
    }

    private static String fetchVideoUrl(String videoId, String format) throws Exception {
        Process process = Runtime.getRuntime().exec("youtube-dl -f " + format + " -g https://www.youtube.com/watch?v=" + videoId);
        int exitCode = process.waitFor();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        }
        return readInputToString(process.getInputStream());
    }
}
