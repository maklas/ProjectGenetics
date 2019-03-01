package ru.maklas.genetics.engine.formulas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class FunctionRenderSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private ImmutableArray<Entity> formulas;
    private Batch batch;
    private BitmapFont font;

    private boolean drawNet;
    private boolean drawPortions;
    private boolean fillNet;
    private boolean drawNumbers;
    private Color netColor = Color.WHITE;
    private Color fillColor = Color.GRAY;
    private Color numberColor = Color.WHITE;

    public FunctionRenderSystem() {
        this(true, true, true, true);
    }

    public FunctionRenderSystem(boolean drawNet, boolean drawPortions, boolean drawNumbers, boolean fillNet) {
        this.drawNet = drawNet;
        this.drawPortions = drawPortions;
        this.drawNumbers = drawNumbers;
        this.fillNet = fillNet;
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
        Gdx.gl.glLineWidth(1);
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        if (drawNet){
            float leftX = Utils.camLeftX(cam);
            float rightX = Utils.camRightX(cam);
            float botY = Utils.camBotY(cam);
            float topY = Utils.camTopY(cam);

            if (fillNet){
                sr.setColor(fillColor);
                float minDelta = Math.min(rightX - leftX, topY - botY);
                double log = Math.log10(minDelta);
                double portionStep = Math.pow(10, Math.floor(log)) * (log - Math.floor(log) > 0.5f ? 1 : 0.5f);

                double xStart = (Math.ceil(leftX / portionStep) * portionStep);
                while (xStart < rightX){
                    float x = (float) xStart;
                    sr.line(x, botY, x, topY);
                    xStart += portionStep;
                }

                double yStart = (Math.ceil(botY / portionStep) * portionStep);
                while (yStart < topY){
                    float y = (float) yStart;
                    sr.line(leftX, y, rightX, y);
                    yStart += portionStep;
                }

            } else
            if (drawPortions){
                sr.setColor(netColor);
                float portionThickness = cam.zoom * 4;
                float minDelta = Math.min(rightX - leftX, topY - botY);
                double log = Math.log10(minDelta);
                double portionStep = Math.pow(10, Math.floor(log)) * (log - Math.floor(log) > 0.5f ? 1 : 0.5f);

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

            sr.setColor(netColor);
            sr.line(leftX, 0, rightX, 0);
            sr.line(0, botY, 0, topY);
        }

        float currentLineWidth = 1f;
        for (Entity formula : formulas) {
            FunctionComponent fc = formula.get(M.fun);
            if (!MathUtils.isEqual(currentLineWidth, fc.lineWidth)){
                sr.flush();
                Gdx.gl.glLineWidth(fc.lineWidth);
                currentLineWidth = fc.lineWidth;
            }
            sr.setColor(fc.color);
            draw(sr, fc.graphFunction, fc.precision);
        }

        sr.end();

        if (!MathUtils.isEqual(currentLineWidth, 1)){
            Gdx.gl.glLineWidth(1f);
        }

        if (drawNet && drawPortions && drawNumbers){
            float leftX = Utils.camLeftX(cam);
            float rightX = Utils.camRightX(cam);
            float botY = Utils.camBotY(cam);
            float topY = Utils.camTopY(cam);

            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            font.setColor(numberColor);
            font.getData().setScale(cam.zoom * 0.75f);

            float portionThickness = cam.zoom * 4;
            float minDelta = Math.min(rightX - leftX, topY - botY);
            double log = Math.log10(minDelta);
            int logFloor = (int) Math.floor(log);
            double portionStep = Math.pow(10, logFloor) * (log - logFloor > 0.5f ? 1 : 0.5f);

            if (topY > -portionThickness && botY < portionThickness) {
                double xStart = (Math.ceil(leftX / portionStep) * portionStep);
                while (xStart < rightX){
                    float x = (float) xStart;

                    String number = log >= 0 ? Long.toString(Math.round(xStart)) : StringUtils.df(xStart, -logFloor);
                    font.draw(batch, number, x + 2 * cam.zoom, 15 * cam.zoom, 10, Align.left, false);
                    xStart += portionStep;
                }
            }

            if (rightX > -portionThickness && leftX < portionThickness) {
                double yStart = (Math.ceil(botY / portionStep) * portionStep);
                while (yStart < topY){
                    float y = (float) yStart;
                    if (!MathUtils.isEqual(y, 0)) {
                        String number = log >= 0 ? Long.toString(Math.round(yStart)) : StringUtils.df(yStart, -logFloor);
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

    public FunctionRenderSystem setFillNet(boolean draw){
        this.fillNet = draw;
        return this;
    }

    public FunctionRenderSystem setNetColor(Color color){
        this.netColor = color;
        return this;
    }

    public FunctionRenderSystem setFillColor(Color color){
        this.fillColor = color;
        return this;
    }

    public FunctionRenderSystem setNumberColor(Color color){
        this.numberColor = color;
        return this;
    }



    private void drawLine(GraphFunction fun, ShapeRenderer sr, float previousX, float x) {
        float prevY = fun.f(previousX);
        float y = fun.f(x);
        sr.line(previousX, prevY, x, y);
    }
}
