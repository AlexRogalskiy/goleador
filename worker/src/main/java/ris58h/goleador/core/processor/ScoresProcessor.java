package ris58h.goleador.core.processor;

import ris58h.goleador.core.Score;
import ris58h.goleador.core.ScoreMatcher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ris58h.goleador.core.IOUtils.readInputToString;

public class ScoresProcessor implements Processor {

    @Override
    public void process(String inName, String outName, String workingDir) throws Exception {
        Path dirPath = Paths.get(workingDir);
        String inGlob = FrameUtils.glob(inName, "txt");
        System.out.println("Extracting scores");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                String text;
                try (InputStream is = new BufferedInputStream(new FileInputStream(path.toFile()))) {
                    text = readInputToString(is);
                }
                text = text.replaceAll("\\s+", " ");
                Score score = ScoreMatcher.find(text);
                String scoreString = score == null ? "" : score.toString();
                Path outPath = FrameUtils.resolveSiblingPath(path, outName, "txt");
                Files.write(outPath, scoreString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
