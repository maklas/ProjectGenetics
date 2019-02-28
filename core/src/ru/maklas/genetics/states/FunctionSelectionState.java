package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.formulas.FunctionComponent;
import ru.maklas.genetics.engine.formulas.FunctionRenderSystem;
import ru.maklas.genetics.engine.formulas.FunctionTrackingRenderSystem;
import ru.maklas.genetics.engine.genetics.EntityUtils;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.rendering.CameraMode;
import ru.maklas.genetics.engine.rendering.CameraSystem;
import ru.maklas.genetics.user_interface.FunctionSelectionView;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.genetics.utils.functions.LinearFunction;
import ru.maklas.genetics.utils.functions.ParabolaFunction;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class FunctionSelectionState extends AbstractEngineState {


    public GraphFunction selectedFunction;
    private FunctionComponent fc;
    private FunctionSelectionView view;
    private ShapeRenderer sr;
    private OrthographicCamera cam;


    public FunctionSelectionState() {
        selectedFunction = new LinearFunction(1);
    }

    public FunctionSelectionState(GraphFunction selectedFunction) {
        this.selectedFunction = selectedFunction;
    }


    @Override
    protected void loadAssets() {
        view = new FunctionSelectionView(selectedFunction);
        view.onFunctionChange(f -> {
            selectedFunction = f;
            fc.graphFunction = f;
        });
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr = new ShapeRenderer();
    }

    @Override
    protected void fillBundler(Bundler bundler) {
        bundler.set(B.sr, sr);
        bundler.set(B.batch, batch);
        bundler.set(B.gsmState, this);
        bundler.set(B.cam, cam);
    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new FunctionRenderSystem());
        engine.add(new EntityDebugSystem().setTextInfoEnabled(false));
        engine.add(new CameraSystem());
        engine.add(new FunctionTrackingRenderSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(EntityUtils.camera(cam, CameraMode.BUTTON_CONTROLLED));
        Entity function = EntityUtils.function(0, selectedFunction, Color.GREEN, 2, 1, true);
        engine.add(function);
        fc = function.get(M.fun);
    }

    @Override
    protected void start() {

    }

    @Override
    protected InputProcessor getInput() {
        return view;
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            popState();
        }
        cam.update();
        engine.update(dt);
        view.act(dt);
    }

    @Override
    public void resize(int width, int height) {
        view.resize(width, height);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
    }

    @Override
    protected void render(Batch batch) {
        engine.render();
        view.draw();
    }
}
