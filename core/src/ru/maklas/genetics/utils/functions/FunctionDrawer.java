package ru.maklas.genetics.utils.functions;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FunctionDrawer {

    public void draw(GraphFunction fun, ShapeRenderer sr, float minX, float maxX, float step){
        float previousX = 0;
        float x = minX;
        while (x < maxX) {
            previousX = x;
            x += step;
            drawLine(fun, sr, previousX, x);
        }

    }

    private void drawLine(GraphFunction fun, ShapeRenderer sr, float previousX, float x) {
        float prevY = fun.f(previousX);
        float y = fun.f(x);
        sr.line(previousX, prevY, x, y);
    }
}
