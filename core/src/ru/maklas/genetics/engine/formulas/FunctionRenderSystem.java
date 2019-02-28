package ru.maklas.genetics.engine.formulas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class FunctionRenderSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private ImmutableArray<Entity> formulas;
    private boolean drawNet;
    private boolean drawPortions;
    private boolean drawNumbers;
    private Batch batch;
    private BitmapFont font;

    public FunctionRenderSystem() {
        this(true, true, true);
    }

    public FunctionRenderSystem(boolean drawNet, boolean drawPortions, boolean drawNumbers) {
        this.drawNet = drawNet;
        this.drawPortions = drawPortions;
        this.drawNumbers = drawNumbers;
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        sr = engine.getBundler().get(B.sr);
        cam = engine.getBundler().get(B.cam);
        batch = engine.getBundler().get(B.batch);
        font = A.images.font;
        formulas = entitiesFor(FunctionComponent.class);
    }

    @Override
    public void render() {
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        if (drawNet){
            float leftX = Utils.camLeftX(cam);
            float rightX = Utils.camRightX(cam);
            float botY = Utils.camBotY(cam);
            float topY = Utils.camTopY(cam);
            sr.setColor(Color.WHITE);

            if (drawPortions){
                float portionThickness = cam.zoom * 4;
                float minDelta = Math.min(rightX - leftX, topY - botY);
                int log = MathUtils.floor((float)Math.log10(minDelta));
                double portionStep = Math.pow(10, log);

                if (topY > -portionThickness && botY < portionThickness) {
                    double xStart = (Math.ceil(leftX / portionStep) * portionStep);
                    while (xStart < rightX){
                        float x = (float) xStart;
                        sr.line(x, -portionThickness, x, portionThickness);
                        xStart += portionStep;
                    }
                }

                if (rightX > -portionThickness && leftX < portionThickness) {
                    double yStart = (Math.ceil(botY / portionStep) * portionStep);
                    while (yStart < topY){
                        float y = (float) yStart;
                        sr.line(-portionThickness, y, portionThickness, y);
                        yStart += portionStep;
                    }
                }
            }

            sr.line(leftX, 0, rightX, 0);
            sr.line(0, botY, 0, topY);
        }

        for (Entity formula : formulas) {
            FunctionComponent fc = formula.get(M.fun);

            sr.setColor(fc.color);
            draw(sr, fc.graphFunction, fc.precision);
        }

        sr.end();

        if (drawNet && drawPortions && drawNumbers){
            float leftX = Utils.camLeftX(cam);
            float rightX = Utils.camRightX(cam);
            float botY = Utils.camBotY(cam);
            float topY = Utils.camTopY(cam);

            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            font.setColor(Color.WHITE);
            font.getData().setScale(cam.zoom);

            float portionThickness = cam.zoom * 4;
            float minDelta = Math.min(rightX - leftX, topY - botY);
            int log = MathUtils.floor((float)Math.log10(minDelta));
            double portionStep = Math.pow(10, log);

            if (topY > -portionThickness && botY < portionThickness) {
                double xStart = (Math.ceil(leftX / portionStep) * portionStep);
                while (xStart < rightX){
                    float x = (float) xStart;

                    String number = log >= 0 ? Long.toString(Math.round(xStart)) : StringUtils.df(xStart, -log);
                    font.draw(batch, number, x + 2 * cam.zoom, 15 * cam.zoom, 10, Align.left, false);
                    xStart += portionStep;
                }
            }

            if (rightX > -portionThickness && leftX < portionThickness) {
                double yStart = (Math.ceil(botY / portionStep) * portionStep);
                while (yStart < topY){
                    float y = (float) yStart;
                    if (!MathUtils.isEqual(y, 0)) {
                        String number = log >= 0 ? Long.toString(Math.round(yStart)) : StringUtils.df(yStart, -log);
                        font.draw(batch, number, 5 * cam.zoom, y + 15 * cam.zoom, 10, Align.left, false);
                    }
                    yStart += portionStep;
                }
            }

            batch.end();
        }

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


    public FunctionRenderSystem setDrawPortions(boolean draw){
        this.drawPortions = draw;
        return this;
    }



    private void drawLine(GraphFunction fun, ShapeRenderer sr, float previousX, float x) {
        float prevY = fun.f(previousX);
        float y = fun.f(x);
        sr.line(previousX, prevY, x, y);
    }
}
