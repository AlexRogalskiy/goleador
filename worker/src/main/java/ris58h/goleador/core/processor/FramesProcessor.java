package ris58h.goleador.core.processor;

import java.util.concurrent.TimeUnit;

import static ris58h.goleador.core.IOUtils.readInputToString;

public class FramesProcessor implements Processor {

    @Override
    public void process(String inName, String outName, String workingDir) throws Exception {
        String command = "ffmpeg -i " + inName +
                " -filter_complex [0:v]crop=in_w/2:in_h/5:0:0[crop];[crop]format=gray" +
                " -r 1 " + workingDir + "/%04d-" + outName + ".png";
        System.out.println("Extracting video frames");
        Process process = Runtime.getRuntime().exec(command);
        if (!process.waitFor(15, TimeUnit.MINUTES)) {
            process.destroyForcibly();
            throw new RuntimeException("Process timeout");
        }
        int exitCode = process.exitValue();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        }
    }
}
