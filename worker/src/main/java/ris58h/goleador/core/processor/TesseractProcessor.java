package ris58h.goleador.core.processor;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TesseractProcessor implements Processor {
    private static final double SPACE_THRESHOLD_FACTOR = 1.2;

    private tesseract.TessBaseAPI api;

    @Override
    public void init(Parameters parameters) throws Exception {
        api = new tesseract.TessBaseAPI();
        String datapath = "tessdata";
        int oem = 1; // we have to set oem to 1 to prevent native crash
        if (api.Init(datapath, "eng", oem) != 0) {
            throw new RuntimeException("Couldn't initialize tesseract!");
        }
    }

    @Override
    public void process(String inName, String outName, String workingDir) throws Exception {
        Path dirPath = Paths.get(workingDir);
        String inGlob = FrameUtils.glob(inName, "png");
        System.out.println("OCR frames");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, inGlob)) {
            for (Path path : stream) {
                Path outPath = FrameUtils.resolveSiblingPath(path, outName, "txt");
                String text = ocr(path.toAbsolutePath().toString());
                Files.write(outPath, text.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private String ocr(String in) {
        List<TextBox> textBoxes = new ArrayList<>();
        int charWidthSum = 0;
        int charCount = 0;
        lept.PIX image = lept.pixRead(in);
        api.SetImage(image);
        api.SetSourceResolution(70);// a hack to get rif of 'Warning. Invalid resolution 0 dpi. Using 70 instead.'
        api.Recognize(null);
        try (tesseract.ResultIterator it = api.GetIterator();
             IntPointer left = new IntPointer(1);
             IntPointer top = new IntPointer(1);
             IntPointer right = new IntPointer(1);
             IntPointer bottom = new IntPointer(1)) {
            if (!it.Empty(tesseract.RIL_SYMBOL)) {
                do {
                    String text;
                    try (BytePointer symbol = it.GetUTF8Text(tesseract.RIL_SYMBOL)) {
                        text = symbol.getString();
                    }
                    it.BoundingBox(tesseract.RIL_SYMBOL, left, top, right, bottom);
                    textBoxes.add(new TextBox(
                            text,
                            left.get(),
                            top.get(),
                            right.get(),
                            bottom.get()
                    ));
                    charWidthSum += right.get() - left.get();
                    charCount += text.length();
                } while (it.Next(tesseract.RIL_SYMBOL));
            }
        }
        lept.pixDestroy(image);
        StringBuilder sb = new StringBuilder();
        if (charCount > 0) {
            int avgCharWidth = charWidthSum / charCount;
            int spaceThreshold = (int) (avgCharWidth * SPACE_THRESHOLD_FACTOR);
            int prevRight = -1;
            for (TextBox textBox : textBoxes) {
                if (prevRight > 0 && textBox.left - prevRight > spaceThreshold) {
                    sb.append(' ');
                }
                sb.append(textBox.text);
                prevRight = textBox.right;
            }
        }
        return sb.toString();
    }

    private static class TextBox {
        final String text;
        final int left;
        final int top;
        final int right;
        final int bottom;

        TextBox(String text, int left, int top, int right, int bottom) {
            this.text = text;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    @Override
    public void dispose() throws Exception {
        if (api != null) {
            api.End();
        }
    }

    public static void main(String[] args) throws Exception {
        TesseractProcessor processor = new TesseractProcessor();
        processor.init(Parameters.empty());
        String text = processor.ocr("test/0182-blur.png");
        System.out.println(text);
    }
}
