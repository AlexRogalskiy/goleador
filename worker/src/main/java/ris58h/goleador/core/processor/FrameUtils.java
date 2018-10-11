package ris58h.goleador.core.processor;

import java.nio.file.Path;

public class FrameUtils {
    public static String glob(String name, String extension) {
        return "[0-9][0-9][0-9][0-9]-" + name + "." + extension;
    }

    public static String resolveSibling(String fileName, String name, String extension) {
        return fileName.substring(0, 5) + name + "." + extension;
    }

    public static Path resolveSiblingPath(Path path, String name, String extension) {
        return path.resolveSibling(resolveSibling(fileName(path), name, extension));
    }

    public static int frameNumber(String fileName) {
        String frameNumberString = fileName.substring(0, 4);
        return Integer.parseInt(frameNumberString);
    }

    public static int frameNumber(Path path) {
        return frameNumber(fileName(path));
    }

    public static String fileName(Path path) {
        return path.getName(path.getNameCount() - 1).toString();
    }
}
