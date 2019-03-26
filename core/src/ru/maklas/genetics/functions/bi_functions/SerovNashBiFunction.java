package ru.maklas.genetics.functions.bi_functions;

import static ru.maklas.genetics.utils.StringUtils.df;

public class SerovNashBiFunction extends GraphBiFunction {

    private final double x;
    private final double y;
    private final double xA;
    private final double yA;

    public SerovNashBiFunction(double x, double y, double xA, double yA) {
        this.x = x;
        this.y = y;
        this.xA = xA;
        this.yA = yA;
    }

    @Override
    public double f(double x1, double x2) {
        double left = x1 - x;
        double right = x2 - y;
        return xA * (left * left) + yA * (right * right);
    }

    @Override
    public double g(double x1, double x2) {
        double xPart = (2 * xA) * (x1 - x);
        double yPart = (2 * yA) * (x2 - y);
        return Math.sqrt(xPart * xPart + yPart * yPart);
    }

    @Override
    public String name() {
        return df(xA, 1) + " * (x1 - " + df(x, 1) + ")^2 + " + df(yA, 1) + " * (x2 - " + df(y, 1) + ")^2";
    }
}
