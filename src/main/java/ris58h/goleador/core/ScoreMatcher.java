package ris58h.goleador.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreMatcher {
    private static final String SEPARATOR_REGEX = "[- ]";
    private static final Pattern SCORE_PATTERN = Pattern.compile(" \\d" + SEPARATOR_REGEX + "\\d ");

    public static Score find(String text) {
        Matcher matcher = SCORE_PATTERN.matcher(text);
        if (matcher.find()) {
            String scoreString = text.substring(matcher.start(), matcher.end());
            String[] split = scoreString.trim().split(SEPARATOR_REGEX);
            return Score.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }
        return null;
    }
}
