package ris58h.goleador.processor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {
    //===== IO =====
    public static String readInputToString(InputStream is) {
        try (Scanner s = new Scanner(new BufferedInputStream(is))) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }
}
