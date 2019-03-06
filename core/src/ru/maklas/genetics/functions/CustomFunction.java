package ru.maklas.genetics.functions;

public class CustomFunction implements GraphFunction {


    @Override
    public double f(double x) {
        return Math.abs(Math.sin(x));
    }

}
