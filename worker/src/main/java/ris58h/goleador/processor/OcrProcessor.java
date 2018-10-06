package ris58h.goleador.processor;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static ris58h.goleador.processor.Utils.readInputToString;

public class OcrProcessor {
    public static void process(String dirName, String inSuffix, String outSuffix) throws Exception {
        Path dirPath = Paths.get(dirName);
        String inPostfix = inSuffix + ".png";
        String inGlob = "[0-9][0-9][0-9][0-9]*" + inPostfix;
        System.out.println("OCR frames");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                String name = path.getName(path.getNameCount() - 1).toString();
                String prefix = name.substring(0, name.length() - inPostfix.length());
                Path outPath = path.resolveSibling(prefix + outSuffix);
                String in = path.toAbsolutePath().toString();
                String out = outPath.toAbsolutePath().toString();
                String command = "tesseract " + in + " " + out;// + " --psm 11";
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
    }
}
