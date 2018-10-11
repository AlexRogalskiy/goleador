package ris58h.goleador.processor;

class LeastSquaresMeasure {
    private double sum = 0;

    public void add(double expected, double actual) {
        double residual = expected - actual;
        this.sum += residual * residual;
    }

    public double result() {
        return this.sum;
    }
}
