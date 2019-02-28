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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;

public class FunctionTrackingRenderSystem extends RenderEntitySystem {

    private ImmutableArray<Entity> functions;
    private OrthographicCamera cam;
    private ShapeRenderer sr;
    private Batch batch;
    private Pool<TrackResult> trackResultPool = new Pool<TrackResult>() {
        @Override
        protected TrackResult newObject() {
            return new TrackResult();
        }
    };
    private Array<TrackResult> trackResults = new Array<>();

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        functions = entitiesFor(FunctionComponent.class);
        cam = engine.getBundler().get(B.cam);
        sr = engine.getBundler().get(B.sr);
        batch = engine.getBundler().get(B.batch);
    }

    @Override
    public void render() {
        Vector2 mouse = Utils.getMouse(cam);

        for (Entity function : functions) {
            FunctionComponent fc = function.get(M.fun);
            if (fc.trackMouse && xWithinCam(mouse.x)){
                trackResults.add(createTrack(fc, mouse));
            }
        }

        if (trackResults.size > 0) {

            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.setProjectionMatrix(cam.combined);
            sr.begin(ShapeRenderer.ShapeType.Line);

            for (TrackResult tr : trackResults) {
                sr.setColor(tr.trackColor);
                sr.getColor().a *= 0.5f;
                sr.line(tr.lineFrom, tr.lineTo);
                if (tr.drawPoint){
                    sr.setColor(Color.PINK);
                    sr.circle(tr.point.x, tr.point.y, 3 * cam.zoom, 6);
                }
            }

            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);


            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            BitmapFont font = A.images.font;

            font.getData().setScale(cam.zoom);

            for (TrackResult tr : trackResults) {
                font.setColor(tr.trackColor);
                font.getColor().a = 1;
                int precision = cam.zoom > 3 ? 1 : cam.zoom > 0.01f ? 2 : 3;
                font.draw(batch, "(" + StringUtils.ff(tr.xVal, precision) + ", " + StringUtils.ff(tr.yVal, precision) + ")", tr.textPos.x, tr.textPos.y, 10, Align.left, false);
            }

            batch.end();

            trackResultPool.freeAll(trackResults);
            trackResults.clear();
        }
    }

    private TrackResult createTrack(FunctionComponent fc, Vector2 mouse) {
        TrackResult tr = trackResultPool.obtain();
        tr.lineFrom.set(mouse);
        float y = fc.graphFunction.f(mouse.x);
        float clampY = clampY(y);
        tr.lineTo.set(mouse.x, clampY);
        tr.trackColor.set(fc.color);
        tr.yVal = y;
        tr.xVal = mouse.x;
        tr.textPos.set(mouse.x, clampY);
        if (clampY == y){
            tr.drawPoint = true;
            tr.point.set(mouse.x, y);
        }
        return tr;
    }

    private float clampX(float x){
        return MathUtils.clamp(x, Utils.camLeftX(cam), Utils.camRightX(cam));
    }

    private float clampY(float y){
        return MathUtils.clamp(y, Utils.camBotY(cam), Utils.camTopY(cam));
    }

    private boolean xWithinCam(float x){
        return x > Utils.camLeftX(cam) && x < Utils.camRightX(cam);
    }

    private class TrackResult implements Pool.Poolable {

        final Vector2 lineFrom = new Vector2();
        final Vector2 lineTo = new Vector2();
        final Vector2 point = new Vector2();
        final Vector2 textPos = new Vector2();
        final Color trackColor = new Color();
        boolean drawPoint = false;
        float yVal;
        float xVal;

        @Override
        public void reset() {
            lineFrom.setZero();
            lineTo.setZero();
            textPos.setZero();
            point.setZero();
            xVal = 0;
            yVal = 0;
            drawPoint = false;
        }
    }
}