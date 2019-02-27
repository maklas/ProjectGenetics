package ru.maklas.genetics.engine.formulas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class FunctionDrawingSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private ImmutableArray<Entity> formulas;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        sr = engine.getBundler().get(B.sr);
        cam = engine.getBundler().get(B.cam);
        formulas = entitiesFor(FunctionComponent.class);
    }

    @Override
    public void render() {

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        for (Entity formula : formulas) {
            FunctionComponent fc = formula.get(M.formula);
            sr.setColor(fc.color);
            draw(sr, fc.graphFunction);
        }


        sr.end();
    }

    private void draw(ShapeRenderer sr, GraphFunction fun) {
        float min = Utils.camLeftX(cam);
        float max = Utils.camRightX(cam);
        float step = cam.zoom;

        float previousX = 0;
        float x = min;
        while (x < max) {
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
