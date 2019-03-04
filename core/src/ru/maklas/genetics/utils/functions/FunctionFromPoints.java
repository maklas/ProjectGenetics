package ru.maklas.genetics.utils.functions;

import com.badlogic.gdx.utils.FloatArray;

public class FunctionFromPoints implements GraphFunction {

    FloatArray floats;

    public FunctionFromPoints(FloatArray floats) {
        this.floats = floats;
    }

    @Override
    public double f(double x) {
        if (x < 0 || x >= floats.size) return 0;
        return floats.get(((int) x));
    }

    public int size(){
        return floats.size;
    }
}
