package ris58h.goleador.core;


import java.nio.file.*;

import static ris58h.goleador.core.Utils.readInputToString;

public class PreOcrProcessor {
    public static void process(String dirName, String inSuffix, String outSuffix) throws Exception {
        Path dirPath = Paths.get(dirName);
        String inPostfix = inSuffix + ".png";
        String inGlob = "[0-9][0-9][0-9][0-9]*" + inPostfix;
        System.out.println("Preparing frames for OCR");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                String name = path.getName(path.getNameCount() - 1).toString();
                String prefix = name.substring(0, name.length() - inPostfix.length());
                Path outPath = path.resolveSibling(prefix + outSuffix + ".png");
                prepare(path, outPath);
            }
        }
    }

    private static void prepare(Path inPath, Path outPath) throws Exception {
//        justCopy(inPath, outPath);
//        resize(inPath, outPath);
        blur(inPath, outPath);
    }

//    private static void justCopy(Path inPath, Path outPath) throws Exception {
//        Files.copy(inPath, outPath, StandardCopyOption.REPLACE_EXISTING);
//    }
//
//    private static void resize(Path inPath, Path outPath) throws Exception {
//        ImageProcessor ip = readImage(inPath.toFile());
//        int imageWidth = ip.getWidth();
//        int imageHeight = ip.getHeight();
//        ImageProcessor resized = ip.resize(2 * imageWidth, 2 * imageHeight);
//        writeImage(resized, outPath.toFile());
//    }

    private static void blur(Path inPath, Path outPath) throws Exception {
        String in = inPath.toAbsolutePath().toString();
        String out = outPath.toAbsolutePath().toString();
        String command = "convert " + in + " -blur 0x1 " + out;
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if (exitCode > 0) {
            throw new RuntimeException(readInputToString(process.getErrorStream()));
        }
    }
}
