package ris58h.goleador.core;

public class ConfusionMatrix<T> {
    private int tp;
    private int tn;
    private int fp;
    private int fn;

    public ConfusionMatrix() {
    }

    public ConfusionMatrix(int tp, int tn, int fp, int fn) {
        this.tp = tp;
        this.tn = tn;
        this.fp = fp;
        this.fn = fn;
    }

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

//    public double accuracy() {
//        return ((double) (tp + tn)) / (tp + tn + fp + fn);
//    }

    public double f1() {
        return 2.0 * tp / (2 * tp + fp + fn);
    }
}
