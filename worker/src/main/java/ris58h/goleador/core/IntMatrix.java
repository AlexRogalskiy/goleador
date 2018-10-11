package ris58h.goleador.core;

public class IntMatrix {
    public final int width;
    public final int height;
    public final int[] buffer;

    public IntMatrix(int width, int height, int[] buffer) {
        if (width * height != buffer.length) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    public IntMatrix(int width, int height) {
        this(width, height, new int[width * height]);
    }
}
