package ris58h.goleador.core;

public class BoolMatrix {
    public final int width;
    public final int height;
    public final boolean[] buffer;

    public BoolMatrix(int width, int height, boolean[] buffer) {
        if (width * height != buffer.length) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    public BoolMatrix(int width, int height) {
        this(width, height, new boolean[width * height]);
    }

    public static BoolMatrix or(BoolMatrix bm1, BoolMatrix bm2) {
        if (bm1.width != bm2.width || bm1.height != bm2.height) {
            throw new IllegalArgumentException();
        }
        BoolMatrix result = new BoolMatrix(bm1.width, bm1.height);
        for (int i = 0; i < bm1.buffer.length; i++) {
            result.buffer[i] = bm1.buffer[i] || bm2.buffer[i];
        }
        return result;
    }

    public static BoolMatrix not(BoolMatrix bm) {
        BoolMatrix result = new BoolMatrix(bm.width, bm.height);
        for (int i = 0; i < bm.buffer.length; i++) {
            result.buffer[i] = !bm.buffer[i];
        }
        return result;
    }
}
