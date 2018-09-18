package ris58h.goleador.core;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ris58h.goleador.core.Utils.readInputToString;

public class ScoresProcessor {

    public static void process(String dirName, String inSuffix, String outFileName) throws Exception {
        Path dirPath = Paths.get(dirName);
        String inPostfix = inSuffix + ".txt";
        String inGlob = "*" + inPostfix;
        SortedMap<Integer, Score> scores = new TreeMap<>();
        System.out.println("Extracting scores");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                String text;
                try (InputStream is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
                    text = readInputToString(is);
                }
                Score score = ScoreMatcher.find(text);
                if (score != null) {
                    String name = path.getName(path.getNameCount() - 1).toString();
                    String prefix = name.substring(0, name.length() - inPostfix.length());
                    int frameNumber = Integer.parseInt(prefix);
                    scores.put(frameNumber, score);
                }
            }
        }

        List<String> timestamps;
        if (scores.isEmpty()) {
            System.out.println("No scores found!");
            timestamps = Collections.emptyList();
        } else {
            System.out.println("Finding timestamps");
            List<Integer> goals = scoreChangedFrames(scores);
            timestamps = goals.stream()
                    .map(frame -> frame - 1) // The first frame is a thumbnail.
                    .map(frame -> frame - 1) //TODO: Is the second one a thumbnail too?!
                    .map(frame -> Integer.max(0, frame - 15)) // We interested in time that is seconds before score changed.
                    .map(Utils::timestamp)
                    .collect(Collectors.toList());
        }
        File timestampsFile = dirPath.resolve(outFileName).toFile();
        try (Writer writer = new BufferedWriter(new FileWriter(timestampsFile))) {
            for (String ts : timestamps) {
                writer.write(ts + "\n");
            }
            writer.flush();
        }
    }

    private static List<Integer> scoreChangedFrames(SortedMap<Integer, Score> scores) {
        List<Integer> goals = new ArrayList<>();
        Score prevScore = Score.of(0, 0); // Score should start with 0-0.
        for (Map.Entry<Integer, Score> entry : scores.entrySet()) {
            Integer time = entry.getKey();
            Score score = entry.getValue();
            if ((score.left == prevScore.left + 1 && score.right == prevScore.right)
                    || (score.left == prevScore.left && score.right == prevScore.right + 1)) {
                goals.add(time);
            }
            prevScore = score;
        }
        return goals;
    }
}
