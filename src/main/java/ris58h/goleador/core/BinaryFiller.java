package ris58h.goleador.core;


import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class BinaryFiller {

    public static void floodFill(BoolMatrix bm, int fromX, int fromY, boolean replacementColor) {
        boolean targetColor = bm.buffer[fromY * bm.width + fromX];
        if (targetColor == replacementColor) {
            return;
        }
        class Node {
            final int x;
            final int y;

            Node(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }
        Queue<Node> nodes = new ArrayDeque<>();
        nodes.add(new Node(fromX, fromY));
        while (!nodes.isEmpty()) {
            Node node = nodes.remove();
            int x = node.x;
            int y = node.y;
            int index = y * bm.width + x;
            if (index < 0 || index >= bm.buffer.length) {
                continue;
            }
            if (bm.buffer[index] != targetColor) {
                continue;
            }
            bm.buffer[index] = replacementColor;
            nodes.add(new Node(x - 1, y));
            nodes.add(new Node(x + 1, y));
            nodes.add(new Node(x, y - 1));
            nodes.add(new Node(x, y + 1));
        }
    }

    public static void fillHoles(BoolMatrix bm, boolean bgColor) {
        boolean fgColor = !bgColor;
        int width = bm.width;
        int height = bm.height;
        boolean[] buffer = bm.buffer;
        boolean[] original = Arrays.copyOf(buffer, buffer.length);
        for (int x = 0; x < width; x++) {
            {
                int y = 0;
                if (buffer[y * width + x] == bgColor) {
                    floodFill(bm, x, 0, fgColor);
                }
            }
            {
                int y = height - 1;
                if (buffer[y * width + x] == bgColor) {
                    floodFill(bm, x, y, fgColor);
                }
            }
        }
        for (int y = 1; y < height - 1; y++) {
            {
                int x = 0;
                if (buffer[y * width + x] == bgColor) {
                    floodFill(bm, x, y, fgColor);
                }
            }
            {
                int x = width - 1;
                if (buffer[y * width + x] == bgColor) {
                    floodFill(bm, x, y, fgColor);
                }
            }
        }
        for (int i = 0; i < buffer.length; i++) {
            bm.buffer[i] = bm.buffer[i] == original[i];
        }
    }
}
