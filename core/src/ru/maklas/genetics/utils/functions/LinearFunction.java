package ru.maklas.genetics.utils.functions;

public class LinearFunction implements GraphFunction {

    public double k;
    public double b;

    public LinearFunction(double k, double b) {
        this.k = k;
        this.b = b;
    }

    @Override
    public double f(double x) {
        return k * x + b;
    }
}
