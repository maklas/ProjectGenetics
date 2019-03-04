package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.formulas.FunctionComponent;
import ru.maklas.genetics.engine.formulas.FunctionRenderSystem;
import ru.maklas.genetics.engine.formulas.FunctionTrackingRenderSystem;
import ru.maklas.genetics.engine.formulas.HistoryRenderSystem;
import ru.maklas.genetics.engine.input.EngineInputAdapter;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.CameraComponent;
import ru.maklas.genetics.engine.rendering.CameraSystem;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.utils.functions.FunctionFromPoints;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class HistoryState extends AbstractEngineState {

    FunctionFromPoints function;
    ShapeRenderer sr;
    OrthographicCamera cam;

    public HistoryState(FunctionFromPoints function) {
        this.function = function;
    }

    @Override
    protected void loadAssets() {
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void fillBundler(Bundler bundler) {
        bundler.set(B.cam, cam);
        bundler.set(B.gsmState, this);
        bundler.set(B.dt, 1 / 60f);
        bundler.set(B.sr, sr);
        bundler.set(B.batch, batch);

    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new EntityDebugSystem().setTextInfoEnabled(false));
        engine.add(new CameraSystem());
        engine.add(new TTLSystem());
        engine.add(new HistoryRenderSystem());
        engine.add(new FunctionRenderSystem()
                .setNetColor(Color.BLACK)
                .setNumberColor(Color.BLACK)
                .setFillNet(false)
                .setFillColor(new Color(0.5f, 0.5f, 0.5f, 1)));
        engine.add(new FunctionTrackingRenderSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, 0).add(new CameraComponent(cam).setControllable()));
        FunctionComponent fc = new FunctionComponent(function);
        fc.color.set(0.75f, 0.23f, 0.23f, 1);
        fc.lineWidth = 1.5f;
        engine.add(new Entity().add(fc));
    }

    @Override
    protected void start() {

    }

    @Override
    protected InputProcessor getInput() {
        return new EngineInputAdapter(engine, cam);
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) popState();
        engine.update(dt);

    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
    }

    @Override
    protected void render(Batch batch) {
        cam.update();
        sr.setProjectionMatrix(cam.combined);
        engine.render();
    }

    @Override
    protected void dispose() {
        super.dispose();
        sr.dispose();
    }
}
