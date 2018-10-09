package ris58h.goleador.processor;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;

import javax.imageio.ImageIO;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {
    //===== IO =====
    public static String readInputToString(InputStream is) {
        try (Scanner s = new Scanner(new BufferedInputStream(is))) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }

    //===== ImageJ =====
    public static ImageProcessor readImage(File file) {
        ImagePlus imagePlus = new Opener().openImage(file.getAbsolutePath());
        return imagePlus.getProcessor();
    }

    public static void writeImage(ImageProcessor ip, File file) throws IOException {
        ImageIO.write(ip.getBufferedImage(), "png", file);
    }
}
