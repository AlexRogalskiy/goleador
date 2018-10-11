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

    public double precision() {
        return ((double) tp) / (tp + fp);
    }

    public double recall() {
        return ((double) tp) / (tp + fn);
    }

    public double result() {
        double precision = precision();
        double recall = recall();
        return 2 * precision * recall * (precision + recall);
    }
}
