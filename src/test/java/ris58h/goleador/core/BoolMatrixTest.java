package ris58h.goleador.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoolMatrixTest {

    @Test
    void or() {
        BoolMatrix bm1 = new BoolMatrix(2, 2, new boolean[]{false, true, true, false});
        BoolMatrix bm2 = new BoolMatrix(2, 2, new boolean[]{true, false, false, false});
        BoolMatrix or = BoolMatrix.or(bm1, bm2);
        assertArrayEquals(new boolean[] {true, true, true, false}, or.buffer);
    }

    @Test
    void not() {
        BoolMatrix bm = new BoolMatrix(2, 2, new boolean[]{false, true, true, false});
        BoolMatrix not = BoolMatrix.not(bm);
        assertArrayEquals(new boolean[] {true, false, false, true}, not.buffer);
    }
}