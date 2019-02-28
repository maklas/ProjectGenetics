package ru.maklas.genetics.engine.formulas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

public class FunctionRenderSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private ImmutableArray<Entity> formulas;
    private boolean drawNet = true;

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

        if (drawNet){
            sr.setColor(Color.WHITE);
            sr.line(Utils.camLeftX(cam), 0, Utils.camRightX(cam), 0);
            sr.line(0, Utils.camBotY(cam), 0, Utils.camTopY(cam));
        }

        for (Entity formula : formulas) {
            FunctionComponent fc = formula.get(M.fun);
            Gdx.gl.glLineWidth(fc.lineWidth);

            sr.setColor(fc.color);
            draw(sr, fc.graphFunction, fc.precision);
        }


        sr.end();

        Gdx.gl20.glLineWidth(1);
    }

    private void draw(ShapeRenderer sr, GraphFunction fun, float precision) {
        float min = Utils.camLeftX(cam);
        float max = Utils.camRightX(cam);
        float step = cam.zoom * precision;

        float previousX = 0;
        float x = min;
        while (x < max) {
            previousX = x;
            x += step;
            drawLine(fun, sr, previousX, x);
        }
    }

    public FunctionRenderSystem setDrawNet(boolean draw){
        this.drawNet = draw;
        return this;
    }



    private void drawLine(GraphFunction fun, ShapeRenderer sr, float previousX, float x) {
        float prevY = fun.f(previousX);
        float y = fun.f(x);
        sr.line(previousX, prevY, x, y);
    }
}
