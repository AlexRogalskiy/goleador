package ris58h.goleador.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Highlighter {
    public static List<Integer> times(List<ScoreFrames> reducedScores) {
        return times(reducedScores, 11);
    }

    static List<Integer> times(List<ScoreFrames> reducedScores, int back) {
        List<Integer> result = new ArrayList<>();
        ScoreFrames prev = null;
        List<ScoreFrames> preparedScores = prepare(reducedScores);
        for (ScoreFrames cur : preparedScores) {
            // Skip first one because it should be 0-0.
            if (prev != null) {
                int highTime = prev.last - back;

                // Time mustn't be negative.
                // The first frame is a thumbnail.
                // Video starts with 0 but frames with 1.
                result.add(Integer.max(0, highTime - 1 - 1));
            }
            prev = cur;
        }
        return result;
    }

    public static List<ScoreFrames> prepare(List<ScoreFrames> reducedScores) {
        return prepare(reducedScores, 2);
    }

    public static List<ScoreFrames> prepare(List<ScoreFrames> reducedScores, int minSeqLength) {
        if (minSeqLength < 1) {
            throw new IllegalArgumentException();
        }
        Stack<ScoreFrames> stack = new Stack<>();
        if (!reducedScores.isEmpty()) {
            for (ScoreFrames cur : reducedScores) {
                if (stack.empty()) {
                    // first score must be 0-0
                    if (!cur.score.equals(Score.of(0, 0))) {
                        continue;
                    }
                    stack.push(copy(cur));
                } else {
                    ScoreFrames prev = stack.peek();
                    if (cur.score.equals(prev.score)) {
                        prev.last = cur.last;
                    } else {
                        int prevCount = prev.last - prev.first + 1;
                        boolean prevTooShort = prevCount < minSeqLength;
                        if (legitScoreChange(prev.score, cur.score)
                                || (prevTooShort && stack.size() > 1 && legitScoreChange(stack.get(stack.size() - 2).score, cur.score))) {
                            if (prevTooShort) {
                                stack.pop();
                                if (stack.isEmpty()) {
                                    stack.push(copy(cur));
                                } else {
                                    ScoreFrames prevPrev = stack.peek();
                                    if (cur.score.equals(prevPrev.score)) {
                                        prevPrev.last = cur.last;
                                    } else {
                                        stack.push(copy(cur));
                                    }
                                }
                            } else {
                                stack.push(copy(cur));
                            }
                        }
                    }
                }
            }
        }
        if (!stack.isEmpty()) {
            ScoreFrames prev = stack.peek();
            int prevCount = prev.last - prev.first + 1;
            if (prevCount < minSeqLength) {
                stack.pop();
            }
        }
        return stack;
    }

    private static boolean legitScoreChange(Score from, Score to) {
        int leftDiff = to.left - from.left;
        int rightDiff = to.right - from.right;
        int diff = leftDiff + rightDiff;
        return 0 == diff || diff == 1;
    }

    private static ScoreFrames copy(ScoreFrames cur) {
        return new ScoreFrames(cur.score, cur.first, cur.last);
    }
}
