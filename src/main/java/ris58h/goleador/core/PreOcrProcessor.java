package ris58h.goleador.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PreOcrProcessor {
    private static final int RGB_BLACK = -16777216;
    private static final int RGB_WHITE = -1;

    public static void process(String dirName, String inSuffix, String outSuffix) throws Exception {
        Path dirPath = Paths.get(dirName);
        String inPostfix = inSuffix + ".png";
        String inGlob = "*" + inPostfix;
        IntMatrix intensityMatrix = null;
        int framesCount = 0;
        System.out.println("Building intensity matrix");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                BufferedImage image = ImageIO.read(path.toFile());
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
                if (intensityMatrix == null) {
                    intensityMatrix = new IntMatrix(imageWidth, imageHeight);
                } else {
                    if (imageWidth != intensityMatrix.width || imageHeight != intensityMatrix.height) {
                        throw new RuntimeException();
                    }
                }
                for (int y = 0; y < intensityMatrix.height; y++) {
                    for (int x = 0; x < intensityMatrix.width; x++) {
                        if (image.getRGB(x, y) == RGB_BLACK) {
                            intensityMatrix.buffer[y * intensityMatrix.width + x] += 1;
                        }
                    }
                }
                framesCount++;
            }
        }

        if (intensityMatrix == null) {
            throw new RuntimeException();
        }

        System.out.println("Thresholding intensity matrix");
        int thresholdBlack = getThreshold(intensityMatrix, framesCount, true);
        BoolMatrix intensityBlack = toBoolMatrix(intensityMatrix, thresholdBlack, true);
        drawBoolMatrix(intensityBlack, dirPath.resolve("intensity-black.png").toFile());
        int thresholdWhite = getThreshold(intensityMatrix, framesCount, false);
        BoolMatrix intensityWhite = toBoolMatrix(intensityMatrix, thresholdWhite, false);
        drawBoolMatrix(intensityWhite, dirPath.resolve("intensity-white.png").toFile());

        int minRectWidth = 10;
        int minRectHeight = 10;
        System.out.println("Detecting rectangles");
        BoolMatrix rectsBlack = RectFiller.detectAndFillRects(intensityBlack, minRectWidth, minRectHeight);
        drawBoolMatrix(rectsBlack, dirPath.resolve("rects-black.png").toFile());
        BoolMatrix rectsWhite = RectFiller.detectAndFillRects(intensityWhite, minRectWidth, minRectHeight);
        drawBoolMatrix(rectsWhite, dirPath.resolve("rects-white.png").toFile());

        int width = intensityMatrix.width;
        int height = intensityMatrix.height;
        System.out.println("Preparing images");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                BufferedImage image = ImageIO.read(path.toFile());
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
                if (imageWidth != width || imageHeight != height) {
                    throw new RuntimeException();
                }
                BoolMatrix outMatrix = new BoolMatrix(width, height);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int i = width * y + x;
                        boolean inBlackRect = rectsBlack.buffer[i];
                        boolean inWhiteRect = rectsWhite.buffer[i];
                        boolean isBlack = image.getRGB(x, y) == RGB_BLACK;
                        if ((inBlackRect && !isBlack) || (inWhiteRect && isBlack)) {
                            outMatrix.buffer[i] = true;
                        }
                    }
                }
                String name = path.getName(path.getNameCount() - 1).toString();
                String prefix = name.substring(0, name.length() - inPostfix.length());
                Path outPath = path.getParent().resolve(prefix + outSuffix + ".png");
                drawBoolMatrix(outMatrix, outPath.toFile());
            }
        }
    }

    private static int getThreshold(IntMatrix intensityMatrix, int framesCount, boolean high) {
        int sum = 0;
        int maxValue = 0;
        for (int intensity : intensityMatrix.buffer) {
            int value = high ? intensity : framesCount - intensity;
            sum += value;
            if (value > maxValue) {
                maxValue = value;
            }
        }
        int avg = sum / intensityMatrix.buffer.length;
        int tr = (avg + maxValue) / 2;
        return high ? tr : framesCount - tr;
    }

    private static BoolMatrix toBoolMatrix(IntMatrix intMatrix, int threshold, boolean high) {
        boolean[] buffer = new boolean[intMatrix.width * intMatrix.height];
        for (int i = 0; i < buffer.length; i++) {
            if (high) {
                if (intMatrix.buffer[i] > threshold) {
                    buffer[i] = true;
                }
            } else {
                if (intMatrix.buffer[i] < threshold) {
                    buffer[i] = true;
                }
            }
        }
        return new BoolMatrix(intMatrix.width, intMatrix.height, buffer);
    }

    private static void drawBoolMatrix(BoolMatrix boolMatrix, File file) throws IOException {
        int width = boolMatrix.width;
        int height = boolMatrix.height;
        BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = boolMatrix.buffer[y * width + x] ? RGB_BLACK : RGB_WHITE;
                outImage.setRGB(x, y, color);
            }
        }
        ImageIO.write(outImage, "png", file);
    }
}
