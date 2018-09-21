package ris58h.goleador.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BinaryFillerTest {

    @Test
    void floodFill() {
        BoolMatrix bm = new BoolMatrix(5, 5, new boolean[]{
                true, true, true, true, true,
                true, false, false, false, false,
                true, false, true, true, true,
                true, false, true, false, true,
                true, false, true, true, true,
        });
        BinaryFiller.floodFill(bm, 1, 1, true);
        boolean[] expectedBuffer = new boolean[]{
                true, true, true, true, true,
                true, true, true, true, true,
                true, true, true, true, true,
                true, true, true, false, true,
                true, true, true, true, true,
        };
        assertArrayEquals(expectedBuffer, bm.buffer);
    }

    @Test
    void floodFill_empty() {
        BoolMatrix bm = new BoolMatrix(5, 5);
        BinaryFiller.floodFill(bm, 0, 0, true);
        boolean[] expectedBuffer = new boolean[25];
        Arrays.fill(expectedBuffer, true);
        assertArrayEquals(expectedBuffer, bm.buffer);
    }

    @Test
    void floodFill_full() {
        BoolMatrix bm = new BoolMatrix(5, 5);
        Arrays.fill(bm.buffer, true);
        BinaryFiller.floodFill(bm, 0, 0, true);
        boolean[] expectedBuffer = new boolean[25];
        Arrays.fill(expectedBuffer, true);
        assertArrayEquals(expectedBuffer, bm.buffer);
    }

    @Test
    void fillHoles() {
        BoolMatrix bm = new BoolMatrix(5, 5, new boolean[]{
                true, true, true, true, true,
                true, false, false, false, false,
                true, false, true, true, true,
                true, false, true, false, true,
                true, false, true, true, true,
        });
        BinaryFiller.fillHoles(bm, false);
        boolean[] expectedBuffer = new boolean[]{
                true, true, true, true, true,
                true, false, false, false, false,
                true, false, true, true, true,
                true, false, true, true, true,
                true, false, true, true, true,
        };
        assertArrayEquals(expectedBuffer, bm.buffer);
    }

    @Test
    void fillHoles_empty() {
        BoolMatrix bm = new BoolMatrix(5, 5);
        BinaryFiller.fillHoles(bm, false);
        boolean[] expectedBuffer = new boolean[25];
        assertArrayEquals(expectedBuffer, bm.buffer);
    }

    @Test
    void fillHoles_full() {
        BoolMatrix bm = new BoolMatrix(5, 5);
        Arrays.fill(bm.buffer, true);
        BinaryFiller.fillHoles(bm, false);
        boolean[] expectedBuffer = new boolean[25];
        Arrays.fill(expectedBuffer, true);
        assertArrayEquals(expectedBuffer, bm.buffer);
    }

}