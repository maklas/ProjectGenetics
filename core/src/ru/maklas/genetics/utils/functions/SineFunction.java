package ru.maklas.genetics.utils.functions;

import com.badlogic.gdx.math.MathUtils;

public class SineFunction implements GraphFunction {

    public float shift = 0;
    public float amp = 0;

    public SineFunction() {

    }

    public SineFunction(float amp, float shift) {
        this.amp = amp;
        this.shift = shift;
    }

    @Override
    public float f(float x) {
        return amp * MathUtils.sin(x +  shift);
    }
}
