package ru.maklas.genetics.utils.functions;

public class LinearFunction implements GraphFunction {

    public float b;

    public LinearFunction(float b) {
        this.b = b;
    }

    @Override
    public float f(float x) {
        return x * b;
    }
}
