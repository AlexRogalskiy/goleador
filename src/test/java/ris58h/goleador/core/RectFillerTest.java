package ris58h.goleador.core;

import org.junit.jupiter.api.Test;
import ris58h.goleador.core.BoolMatrix;
import ris58h.goleador.core.RectFiller;

import static org.junit.jupiter.api.Assertions.*;

class RectFillerTest {

    @Test
    void fill1() {
        BoolMatrix boolMatrix = new BoolMatrix(3, 3, new boolean[]{
                true, true, true,
                true, false, true,
                true, true, true
        });
        BoolMatrix fillled = RectFiller.detectAndFillRects(boolMatrix, 3, 3);
        boolean[] expectedBuffer = new boolean[]{
                true, true, true,
                true, true, true,
                true, true, true
        };
        assertArrayEquals(expectedBuffer, fillled.buffer);
    }

    @Test
    void fill2() {
        BoolMatrix boolMatrix = new BoolMatrix(4, 4, new boolean[]{
                true, true, true, true,
                true, false, true, true,
                true, true, true, true,
                true, true, true, false
        });
        BoolMatrix filled = RectFiller.detectAndFillRects(boolMatrix, 3, 3);
        boolean[] expectedBuffer = new boolean[]{
                true, true, true, true,
                true, true, true, true,
                true, true, true, true,
                true, true, true, false
        };
        assertArrayEquals(expectedBuffer, filled.buffer);
    }

    @Test
    void isRect1() {
        BoolMatrix boolMatrix = new BoolMatrix(3, 3, new boolean[]{
                true, true, true,
                true, false, true,
                true, true, true
        });
        assertTrue(RectFiller.isRect(boolMatrix, 0, 0, 2, 2));
    }

    @Test
    void isRect2() {
        BoolMatrix boolMatrix = new BoolMatrix(3, 3, new boolean[]{
                true, true, true,
                true, false, true,
                true, true, false
        });
        assertFalse(RectFiller.isRect(boolMatrix, 0, 0, 2, 2));
    }
}