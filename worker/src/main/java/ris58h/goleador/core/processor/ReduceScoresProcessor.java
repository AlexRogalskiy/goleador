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
                        break;
                    }
                    stack.push(new ScoreFrames(score, frame, frame));
                } else {
                    ScoreFrames prev = stack.peek();
                    if (score.equals(prev.score)) {
                        prev.last = frame;
                    } else {
                        int leftDiff = Math.abs(score.left - prev.score.left);
                        int rightDiff = Math.abs(score.right - prev.score.right);
                        int diff = leftDiff + rightDiff;
                        if (diff < 2) {
                            int prevCount = prev.last - prev.first + 1;
                            if (prevCount < minSeqLength) {
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
}
