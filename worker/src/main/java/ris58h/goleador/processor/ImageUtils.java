package ris58h.goleador.processor;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    //===== ImageJ =====
    public static ImageProcessor readImage(File file) {
        ImagePlus imagePlus = new Opener().openImage(file.getAbsolutePath());
        return imagePlus.getProcessor();
    }

    public static void writeImage(ImageProcessor ip, File file) throws IOException {
        ImageIO.write(ip.getBufferedImage(), "png", file);
    }
}
