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
        List<ScoreFrames> result = new ArrayList<>();
        if (!scores.isEmpty()) {
            Score prevScore = null;
            Integer first = null;
            Integer last = null;
            for (Map.Entry<Integer, Score> entry : scores.entrySet()) {
                Integer frame = entry.getKey();
                Score score = entry.getValue();
                if (prevScore == null) {
                    // first score must be 0-0
                    if (score.equals(Score.of(0, 0))) {
                        prevScore = score;
                    } else {
                        break;
                    }
                }
                if (first == null) {
                    first = frame;
                }
                int leftDiff = score.left - prevScore.left;
                int rightDiff = score.right - prevScore.right;
                int diff = leftDiff + rightDiff;
                if (diff <= 1) {
                    if (diff > 0) {
                        if (last != null) {
                            result.add(new ScoreFrames(prevScore, first, last));
                        }
                        first = frame;
                    }
                    prevScore = score;
                    last = frame;
                }
            }
            if (first != null && last != null) {
                result.add(new ScoreFrames(prevScore, first, last));
            }
        }
        return result;
    }
}
