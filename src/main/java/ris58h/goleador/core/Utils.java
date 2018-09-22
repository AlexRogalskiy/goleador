package ris58h.goleador.core;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {
    public static String readInputToString(InputStream is) {
        try (Scanner s = new Scanner(new BufferedInputStream(is))) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }


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
