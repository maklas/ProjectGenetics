package ru.maklas.genetics.utils.functions;

import com.badlogic.gdx.math.MathUtils;

public class DampedSineWaveFunction implements GraphFunction {

    public double amp;
    public double waveLen;
    public double shift;
    public double decay;

    /**
     * @param amp Amplitude
     * @param waveLen distance between peaks
     * @param shift shift in Y position
     * @param decay 0..1 speed of decay
     */
    public DampedSineWaveFunction(double amp, double waveLen, double shift, double decay) {
        this.amp = amp;
        this.waveLen = waveLen;
        this.shift = shift;
        this.decay = decay;
    }

    @Override
    public double f(double x) {
        return (amp * Math.pow(MathUtils.E, (-decay * x)) * Math.sin((2 * MathUtils.PI * x/waveLen)) + shift);
    }
}
