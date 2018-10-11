package ris58h.goleador.processor;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static ris58h.goleador.processor.Utils.readInputToString;

public class OcrProcessor implements Processor {

    @Override
    public void process(String inName, String outName, String workingDir) throws Exception {
        Path dirPath = Paths.get(workingDir);
        String inGlob = FrameUtils.glob(inName, "png");
        System.out.println("OCR frames");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                Path outPath = FrameUtils.resolveSiblingPath(path, outName, "png");
                String in = path.toAbsolutePath().toString();
                String out = outPath.toAbsolutePath().toString();
                ocr(in, out);
            }
        }
    }

    private void ocr(String in, String out) throws IOException, InterruptedException {
        String tessOut = out.substring(0, out.length() - 4); // tesseract adds '.txt' automatically
        String command = "tesseract " + in + " " + tessOut; // + " --psm 11";
        Process process = Runtime.getRuntime().exec(command);
        if (!process.waitFor(1, TimeUnit.MINUTES)) {
            throw new RuntimeException("Process timeout");
        }
        int exitCode = process.exitValue();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        }
    }
}
