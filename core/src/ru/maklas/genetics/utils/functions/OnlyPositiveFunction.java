package ru.maklas.genetics.utils.functions;

public class OnlyPositiveFunction implements GraphFunction {


    GraphFunction delegate;
    float negativeY = 0;

    public OnlyPositiveFunction(GraphFunction delegate) {
        this.delegate = delegate;
    }

    public OnlyPositiveFunction(GraphFunction delegate, float negativeY) {
        this.delegate = delegate;
        this.negativeY = negativeY;
    }

    @Override
    public float f(float x) {
        return x < 0 ? negativeY : delegate.f(x);
    }
}
