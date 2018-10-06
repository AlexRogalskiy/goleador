package ris58h.goleador.processor;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;

import javax.imageio.ImageIO;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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


    //===== Timestamps =====
    public static String timestamp(int timeInSeconds) {
        StringBuilder sb = new StringBuilder();
        int seconds = timeInSeconds % 60;
        int timeInMinutes = timeInSeconds / 60;
        int minutes = timeInMinutes % 60;
        int timeInHours = timeInMinutes / 60;
        if (timeInHours > 0) {
            sb.append(timeInHours).append(':');
            if (minutes < 10) {
                sb.append('0');
            }
        }
        sb.append(minutes).append(':');
        if (seconds < 10) {
            sb.append('0');
        }
        sb.append(seconds);
        return sb.toString();
    }
}
