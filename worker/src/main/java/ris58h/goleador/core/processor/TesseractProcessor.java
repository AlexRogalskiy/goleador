package ris58h.goleador.core.processor;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TesseractProcessor implements Processor {
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
        String result;
        lept.PIX image = lept.pixRead(in);
        api.SetImage(image);
        api.SetSourceResolution(70);// a hack to get rif of 'Warning. Invalid resolution 0 dpi. Using 70 instead.'
        try (BytePointer text = api.GetUTF8Text()) {
            result = text.getString();
        }
        lept.pixDestroy(image);
        return result;
    }

    @Override
    public void dispose() throws Exception {
        if (api != null) {
            api.End();
        }
    }
}
