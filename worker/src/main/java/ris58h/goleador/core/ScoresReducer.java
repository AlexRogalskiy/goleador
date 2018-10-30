package ris58h.goleador.core;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;

public class ScoresReducer {
    public static List<ScoreFrames> reduceScores(SortedMap<Integer, Score> scores) {
        return reduceScores(scores, 2);
    }

    public static List<ScoreFrames> reduceScores(SortedMap<Integer, Score> scores, int minSeqLength) {
        if (minSeqLength < 1) {
            throw new IllegalArgumentException();
        }
        Stack<ScoreFrames> stack = new Stack<>();
        if (!scores.isEmpty()) {
            for (Map.Entry<Integer, Score> entry : scores.entrySet()) {
                Integer frame = entry.getKey();
                Score score = entry.getValue();
                if (stack.empty()) {
                    // first score must be 0-0
                    if (!score.equals(Score.of(0, 0))) {
                        continue;
                    }
                    stack.push(new ScoreFrames(score, frame, frame));
                } else {
                    ScoreFrames prev = stack.peek();
                    if (score.equals(prev.score)) {
                        prev.last = frame;
                    } else {
                        int prevCount = prev.last - prev.first + 1;
                        boolean prevTooShort = prevCount < minSeqLength;
                        if (legitScoreChange(prev.score, score)
                                || (prevTooShort && stack.size() > 1 && legitScoreChange(stack.get(stack.size() - 2).score, score))) {
                            if (prevTooShort) {
                                stack.pop();
                                if (stack.isEmpty()) {
                                    stack.push(new ScoreFrames(score, frame, frame));
                                } else {
                                    ScoreFrames prevPrev = stack.peek();
                                    if (score.equals(prevPrev.score)) {
                                        prev.last = frame;
                                    } else {
                                        stack.push(new ScoreFrames(score, frame, frame));
                                    }
                                }
                            } else {
                                stack.push(new ScoreFrames(score, frame, frame));
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
}
