package ris58h.goleador.core;


public class RectFiller {

    public static BoolMatrix detectAndFillRects(BoolMatrix boolMatrix, int minRectWidth, int minRectHeight) {
        if (minRectWidth < 3 || minRectHeight < 3) {
            throw new IllegalArgumentException();
        }
        BoolMatrix result = new BoolMatrix(boolMatrix.width, boolMatrix.height);
        for (int y1 = 0; y1 < boolMatrix.height; y1++) {
            for (int x1 = 0; x1 < boolMatrix.width; x1++) {
                for (int y2 = y1 + minRectHeight - 1; y2 < boolMatrix.height; y2++) {
                    for (int x2 = x1 + minRectWidth - 1; x2 < boolMatrix.width; x2++) {
                        if (isRect(boolMatrix, x1, y1, x2, y2)) {
                            drawRect(result, x1, y1, x2, y2);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static boolean isRect(BoolMatrix boolMatrix, int x1, int y1, int x2, int y2) {
        boolean[] buffer = boolMatrix.buffer;
        int width = boolMatrix.width;
        for (int x = x1; x <= x2; x++) {
            if (!buffer[y1 * width + x] || !buffer[y2 * width + x]) {
                return false;
            }
        }
        for (int y = y1 + 1; y < y2; y++) {
            if (!buffer[y * width + x1] || !buffer[y * width + x2]) {
                return false;
            }
        }
        return true;
    }

    private static void drawRect(BoolMatrix boolMatrix, int x1, int y1, int x2, int y2) {
        boolean[] buffer = boolMatrix.buffer;
        int width = boolMatrix.width;
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                buffer[y * width + x] = true;
            }
        }
    }
}
