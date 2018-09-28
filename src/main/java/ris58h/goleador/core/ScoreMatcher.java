package ris58h.goleador.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreMatcher {
    private static final String SEPARATOR_REGEX = "( ?[- :] ?)";
    private static final String PROBABLY_ZERO_REGEX = "[QOo]";
    private static final String SCORE_DIGIT_REGEX = "(\\d|" + PROBABLY_ZERO_REGEX + ")";
    private static final Pattern SCORE_PATTERN = Pattern.compile("(^|\\W)"
            + SCORE_DIGIT_REGEX
            + SEPARATOR_REGEX
            + SCORE_DIGIT_REGEX
            + "(\\W|$)");

    public static Score find(String text) {
        Matcher matcher = SCORE_PATTERN.matcher(text);
        if (matcher.find()) {
            String scoreString = text.substring(matcher.start(), matcher.end());
            String cleanedScoreString = scoreString
                    .replaceAll("\\W", " ")
                    .replaceAll(PROBABLY_ZERO_REGEX, "0")
                    .trim();
            String[] split = cleanedScoreString.split(SEPARATOR_REGEX);
            return Score.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }
        return null;
    }
}
