package ru.maklas.genetics.utils.functions;

import static java.lang.Math.*;

public class CustomFunction implements GraphFunction {

    @Override
    public double f(double x) {
        return sin(4 * x) * cos(4 * x) + tan(x);
    }

}
