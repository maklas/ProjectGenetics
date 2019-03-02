package ru.maklas.genetics.utils.functions;

public class OnlyPositiveFunction implements GraphFunction {

    public GraphFunction delegate;
    double negativeY = 0;

    public OnlyPositiveFunction(GraphFunction delegate) {
        this.delegate = delegate;
    }

    public OnlyPositiveFunction(GraphFunction delegate, double negativeY) {
        this.delegate = delegate;
        this.negativeY = negativeY;
    }

    @Override
    public double f(double x) {
        return x < 0 ? negativeY : delegate.f(x);
    }
}
