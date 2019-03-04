package ru.maklas.genetics.utils.functions;

public class CustomFunction implements GraphFunction {


    @Override
    public double f(double x) {
        return Math.abs(Math.sin(x));
    }

}
