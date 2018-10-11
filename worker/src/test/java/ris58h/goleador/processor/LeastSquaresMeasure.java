package ris58h.goleador.processor;

class LeastSquaresMeasure {
    private long sum = 0;

    void add(long expected, long actual) {
        long residual = expected - actual;
        this.sum += residual * residual;
    }

    Number computeResult() {
        return this.sum;
    }
}
