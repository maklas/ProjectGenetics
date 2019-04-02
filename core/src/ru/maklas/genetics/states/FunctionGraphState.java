package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.assets.Asset;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.formulas.FunctionComponent;
import ru.maklas.genetics.engine.genetics.EntityUtils;
import ru.maklas.genetics.engine.input.EngineInputAdapter;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.CameraMode;
import ru.maklas.genetics.engine.rendering.CameraSystem;
import ru.maklas.genetics.engine.rendering.FunctionRenderSystem;
import ru.maklas.genetics.engine.rendering.FunctionTrackingRenderSystem;
import ru.maklas.genetics.functions.FunctionUtils;
import ru.maklas.genetics.functions.GraphFunction;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntitySystem;

public class FunctionGraphState extends AbstractEngineState {

    private final Array<GraphFunction> functions;
    private OrthographicCamera cam;
    private ShapeRenderer sr;

    public FunctionGraphState(Array<GraphFunction> functions) {
        this.functions = functions;
    }

    @Override
    protected void loadAssets() {
        A.all().foreach(Asset::load);
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void fillBundler(Bundler bundler) {
        bundler.set(B.cam, cam);
        bundler.set(B.batch, batch);
        bundler.set(B.gsmState, this);
        bundler.set(B.sr, sr);
    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new CameraSystem());
        engine.add(new FunctionTrackingRenderSystem().setEnableTracking(true).setPrintXY(true));
        engine.add(new EntityDebugSystem().setTextInfoEnabled(false).setZoomAtMouse(true));
        engine.add(new FunctionRenderSystem().setDrawFunctions(true).setDrawNet(true).setDrawPortions(true).setFillNet(false).setNetColor(Color.BLACK).setNumberColor(Color.BLACK));
        engine.add(new UpdatableEntitySystem());
        engine.add(new TTLSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(EntityUtils.camera(cam, CameraMode.BUTTON_CONTROLLED));
    }

    @Override
    protected void start() {
        for (int i = 0; i < functions.size; i++) {
            FunctionComponent fc = new FunctionComponent(functions.get(i));
            fc.color = FunctionUtils.goodFunctionColor(i);
            fc.lineWidth = 2f;
            engine.add(new Entity().add(fc));
        }
    }

    @Override
    protected void update(float dt) {
        engine.update(dt);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            popState();
        }
    }

    @Override
    protected InputProcessor getInput() {
        return new EngineInputAdapter(engine, cam);
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(width, height);
    }

    @Override
    protected void render(Batch batch) {
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        sr.setProjectionMatrix(cam.combined);
        engine.render();
    }
}
