package ris58h.goleador.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreMatcher {
    private static final String TIME_REGEX = "\\d?\\d:\\d\\d";

    private static final String SEPARATOR_REGEX = "( ?[- :=] ?)";
    private static final String PROBABLY_ZERO_REGEX = "[QOo]";
    private static final String SCORE_DIGIT_REGEX = "(\\d|" + PROBABLY_ZERO_REGEX + ")";
    private static final String LEFT_REGEX = "(^|\\W)(?<left>" + SCORE_DIGIT_REGEX + ")";
    private static final String RIGHT_REGEX = "(?<right>" + SCORE_DIGIT_REGEX + ")(\\W|$)";
    private static final Pattern SCORE_PATTERN = Pattern.compile(LEFT_REGEX
            + SEPARATOR_REGEX
            + RIGHT_REGEX);
    private static final Pattern NO_SEPARATOR_SCORE_PATTERN = Pattern.compile(LEFT_REGEX
            + RIGHT_REGEX);

    public static Score find(String text) {
        String noTime = text.replaceAll(TIME_REGEX, "");
        return findScore(noTime);
    }

    private static Score findScore(String text) {
        Score score = findScore(text, SCORE_PATTERN);
        if (score == null) {
            score = findScore(text, NO_SEPARATOR_SCORE_PATTERN);
        }
        return score;
    }

    private static Score findScore(String text, Pattern pattern) {
        Matcher withSeparatorMatcher = pattern.matcher(text);
        if (withSeparatorMatcher.find()) {
            String left = withSeparatorMatcher.group("left").replaceAll(PROBABLY_ZERO_REGEX, "0");
            String right = withSeparatorMatcher.group("right").replaceAll(PROBABLY_ZERO_REGEX, "0");
            return Score.of(Integer.parseInt(left), Integer.parseInt(right));
        }
        return null;
    }
}
