package ris58h.goleador.processor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfusionMatrixTest {
    @Test
    void test() {
        ConfusionMatrix confusionMatrix = new ConfusionMatrix(63, 72, 28, 37);
        assertEquals(0.63, confusionMatrix.recall(), 0.01);
//        assertEquals(0.28, confusionMatrix.fpr(), 0.01);
        assertEquals(0.69, confusionMatrix.precision(), 0.01);
        assertEquals(0.66, confusionMatrix.f1(), 0.01);
//        assertEquals(0.68, confusionMatrix.accuracy(), 0.01);
    }
}
