package ru.maklas.genetics.utils.functions;

public class LinearFunction implements GraphFunction {

    public float k;
    public float b;

    public LinearFunction(float k, float b) {
        this.k = k;
        this.b = b;
    }

    @Override
    public float f(float x) {
        return k * x + b;
    }
}
