package ris58h.goleador.core.processor;

import ris58h.goleador.core.Score;
import ris58h.goleador.core.ScoreFrames;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ris58h.goleador.core.IOUtils.readInputToString;

public class ReduceScoresProcessor implements Processor {

    @Override
    public void process(String inName, String outName, String workingDir) throws Exception {
        Path dirPath = Paths.get(workingDir);
        String inGlob = FrameUtils.glob(inName, "txt");
        SortedMap<Integer, Score> scores = new TreeMap<>();
        System.out.println("Loadings scores");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                String scoreString;
                try (InputStream is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
                    scoreString = readInputToString(is);
                }
                if (!scoreString.isEmpty()) {
                    Score score = Score.parseScore(scoreString);
                    int frameNumber = FrameUtils.frameNumber(path);
                    scores.put(frameNumber, score);
                }
            }
        }
        List<ScoreFrames> reducedScores = reduceScores(scores);
        List<String> lines = reducedScores.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        Files.write(dirPath.resolve(outName + ".txt"), lines, Charset.forName("UTF-8"));
    }

    static List<ScoreFrames> reduceScores(SortedMap<Integer, Score> scores) {
        return reduceScores(scores, 2);
    }

    static List<ScoreFrames> reduceScores(SortedMap<Integer, Score> scores, int minSeqLength) {
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
