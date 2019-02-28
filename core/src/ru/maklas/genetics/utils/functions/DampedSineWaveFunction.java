package ru.maklas.genetics.utils.functions;

import com.badlogic.gdx.math.MathUtils;

public class DampedSineWaveFunction implements GraphFunction {

    public float amp;
    public float waveLen;
    public float shift;
    public float decay;

    /**
     * @param amp Amplitude
     * @param waveLen distance between peaks
     * @param shift shift in Y position
     * @param decay 0..1 speed of decay
     */
    public DampedSineWaveFunction(float amp, float waveLen, float shift, float decay) {
        this.amp = amp;
        this.waveLen = waveLen;
        this.shift = shift;
        this.decay = decay;
    }

    @Override
    public float f(float x) {
        return (float)(amp * Math.pow(MathUtils.E, (-decay * x)) * MathUtils.sin((2* MathUtils.PI * x/waveLen))+shift);
    }
}
