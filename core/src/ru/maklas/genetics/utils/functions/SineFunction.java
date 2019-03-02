package ru.maklas.genetics.utils.functions;

import com.badlogic.gdx.math.MathUtils;

public class SineFunction implements GraphFunction {

    public double shift = 0;
    public double amp = 0;
    public double waveLen = 0;

    public SineFunction() {

    }

    public SineFunction(double amp, double waveLen, double shift) {
        this.amp = amp;
        this.waveLen = waveLen;
        this.shift = shift;
    }

    @Override
    public double f(double x) {
        return amp * Math.sin((2 * MathUtils.PI * (x / waveLen)) + shift);
    }
}
