package ru.maklas.genetics.utils.functions;

public class ParabolaFunction implements GraphFunction {

    float a;
    float b;
    float c;

    public ParabolaFunction(float a, float b, float c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public float f(float x) {
        return a * (x * x) + (b * x) + c;
    }
}
