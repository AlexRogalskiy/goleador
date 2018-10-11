package ris58h.goleador.worker;

import ris58h.goleador.core.IOUtils;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

public class SimpleVideoUrlFetcher {
    private static final String FORMATS_KEY = "adaptive_fmts";

    public static String fetchFor(String videoId, String format) throws Exception {
        //TODO https
        URL url = new URL("http://www.youtube.com/get_video_info?video_id=" + videoId + "&el=embedded&ps=default&eurl=&gl=US&hl=en");
        HttpURLConnection con = null;
        String response;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            response = IOUtils.readInputToString(con.getInputStream());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return extractFromResponse(response, format);
    }

    static String extractFromResponse(String response, String format) throws Exception {
        int formatsStart = response.indexOf(FORMATS_KEY + "=");
        if (formatsStart >= 0) {
            int formatsValueStart = formatsStart + FORMATS_KEY.length() + 1;
            int formatsEnd = response.indexOf('&', formatsValueStart);
            int formatsValueEnd = formatsEnd < 0 ? response.length() : formatsEnd;
            String formatsEncodedValue = response.substring(formatsValueStart, formatsValueEnd);
            String formatsValue = decode(formatsEncodedValue);
            String[] entries = formatsValue.split(",");
            for (String entry : entries) {
                String[] subEntries = entry.split("&");
                String url = null;
                boolean formatFound = false;
                for (String subEntry : subEntries) {
                    String[] kv = subEntry.split("=");
                    String key = kv[0];
                    String rawValue = kv.length == 1 ? "" : kv[1];
                    String value = decode(rawValue);
                    if (key.equals("itag")) {
                        if (value.equals(format)) {
                            if (url != null) {
                                return url;
                            }
                            formatFound = true;
                        } else {
                            break;
                        }
                    }
                    if (key.equals("url")) {
                        if (formatFound) {
                            return value;
                        } else {
                            url = value;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String decode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, "UTF-8");
    }
}
