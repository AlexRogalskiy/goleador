package ris58h.goleador.processor;

import static ris58h.goleador.processor.Utils.readInputToString;

public class FramesProcessor {
    public static void process(String dirName, String input, String suffix) throws Exception {
        String command = "ffmpeg -i " + input +
                " -filter_complex [0:v]crop=in_w/2:in_h/5:0:0[crop];[crop]format=gray" +
                " -r 1 " + dirName + "/%04d" + suffix + ".png";
        System.out.println("Extracting video frames");
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        }
    }
}
