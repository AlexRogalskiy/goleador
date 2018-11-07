package ris58h.goleador.main;

public class Utils {
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
