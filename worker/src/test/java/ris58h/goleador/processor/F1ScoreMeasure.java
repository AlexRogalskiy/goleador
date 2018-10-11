package ris58h.goleador.processor;

public class F1ScoreMeasure<T> {
    private int tp;
    private int tn;
    private int fp;
    private int fn;

    public void add(T expected, T actual) {
        if (actual == null) {
            if (expected == null) {
                tn++;
            } else {
                fn++;
            }
        } else {
            if (actual.equals(expected)) {
                tp++;
            } else {
                fp++;
            }
        }
    }

    public float computePrecision() {
        return ((float) tp) / (tp + fp);
    }

    public float computeRecall() {
        return ((float) tp) / (tp + fn);
    }

    public Number computeResult() {
        float precision = computePrecision();
        float recall = computeRecall();
        return 2 * precision * recall * (precision + recall);
    }
}
