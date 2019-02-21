package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.MovementSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.AnimationSystem;
import ru.maklas.genetics.engine.rendering.CameraComponent;
import ru.maklas.genetics.engine.rendering.CameraSystem;
import ru.maklas.genetics.engine.rendering.RenderingSystem;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.genetics.utils.functions.FunctionDrawer;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.genetics.utils.functions.ParabolaFunction;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntitySystem;

public class GeneticsGenerationState extends AbstractEngineState {

    ShapeRenderer sr;
    OrthographicCamera cam;
    FunctionDrawer drawer;
    GraphFunction function;

    @Override
    protected void loadAssets() {
        sr = new ShapeRenderer();
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        drawer = new FunctionDrawer();
        function = new ParabolaFunction(0.1f, 0, 0);
    }

    @Override
    protected void fillBundler(Bundler bundler) {
        bundler.set(B.cam, cam);
        bundler.set(B.batch, batch);
        bundler.set(B.gsmState, this);
        bundler.set(B.dt, 1 / 60f);
    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new RenderingSystem());
        engine.add(new UpdatableEntitySystem());
        engine.add(new AnimationSystem());
        engine.add(new EntityDebugSystem());
        engine.add(new CameraSystem());
        engine.add(new MovementSystem());
        engine.add(new TTLSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, 0).add(new CameraComponent(cam).setControllable()));
    }

    @Override
    protected void start() { }

    @Override
    protected void update(float dt) {
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

        Gdx.gl20.glLineWidth(2f);
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        drawNet();

        sr.setColor(Color.CYAN);
        drawer.draw(function, sr, Utils.camLeftX(cam), Utils.camRightX(cam), cam.zoom);

        sr.end();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        engine.render();
        batch.end();
    }

    private void drawNet() {
        sr.setColor(Color.WHITE);
        sr.line(Utils.camLeftX(cam), 0, Utils.camRightX(cam), 0);
        sr.line(0, Utils.camBotY(cam), 0, Utils.camTopY(cam));
    }

    @Override
    protected void dispose() {
        super.dispose();
        sr.dispose();
    }
}
