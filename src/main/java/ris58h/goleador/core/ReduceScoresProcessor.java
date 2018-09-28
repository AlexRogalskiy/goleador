package ris58h.goleador.core;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ris58h.goleador.core.Utils.readInputToString;

public class ReduceScoresProcessor {
    public static void process(String dirName, String inSuffix, String outFileName) throws Exception {
        Path dirPath = Paths.get(dirName);
        String inPostfix = inSuffix + ".txt";
        String inGlob = "[0-9][0-9][0-9][0-9]*" + inPostfix;
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
                    String name = path.getName(path.getNameCount() - 1).toString();
                    String prefix = name.substring(0, name.length() - inPostfix.length());
                    int frameNumber = Integer.parseInt(prefix);
                    scores.put(frameNumber, score);
                }
            }
        }
        List<ScoreFrames> reducedScores = reduceScores(scores);
        List<String> lines = reducedScores.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        Files.write(dirPath.resolve(outFileName), lines, Charset.forName("UTF-8"));
    }

    static List<ScoreFrames> reduceScores(SortedMap<Integer, Score> scores) {
        List<ScoreFrames> result = new ArrayList<>();
        if (!scores.isEmpty()) {
            Score prevScore = Score.of(0, 0);
            Integer first = null;
            Integer last = null;
            for (Map.Entry<Integer, Score> entry : scores.entrySet()) {
                Integer frame = entry.getKey();
                Score score = entry.getValue();
                if (first == null) {
                    first = frame;
                }
                boolean leftSame = score.left == prevScore.left;
                boolean leftChanged = score.left == prevScore.left + 1;
                boolean rightSame = score.right == prevScore.right;
                boolean rightChanged = score.right == prevScore.right + 1;
                if ((leftSame || leftChanged) && (rightSame || rightChanged)) {
                    if ((leftChanged && rightSame) || (leftSame && rightChanged)) {
                        result.add(new ScoreFrames(prevScore, first, last));
                        first = frame;
                    }
                    prevScore = score;
                }
                last = frame;
            }
            result.add(new ScoreFrames(prevScore, first, last));
        }
        return result;
    }
}
