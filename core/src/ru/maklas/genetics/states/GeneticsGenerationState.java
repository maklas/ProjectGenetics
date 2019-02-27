package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.formulas.FunctionComponent;
import ru.maklas.genetics.engine.formulas.FunctionDrawingSystem;
import ru.maklas.genetics.engine.genetics.ChromosomeRenderSystem;
import ru.maklas.genetics.engine.genetics.ChromosomeSystem;
import ru.maklas.genetics.engine.genetics.dispatchable.EvolveRequest;
import ru.maklas.genetics.engine.genetics.dispatchable.ResetEvolutionRequest;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.MovementSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.AnimationSystem;
import ru.maklas.genetics.engine.rendering.CameraComponent;
import ru.maklas.genetics.engine.rendering.CameraSystem;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.tests.Crossover;
import ru.maklas.genetics.tests.CrossoverEvolutionManager;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.genetics.utils.functions.ParabolaFunction;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntitySystem;

public class GeneticsGenerationState extends AbstractEngineState {

    private ShapeRenderer sr;
    private OrthographicCamera cam;

    @Override
    protected void loadAssets() {
        sr = new ShapeRenderer();
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void fillBundler(Bundler bundler) {
        bundler.set(B.cam, cam);
        bundler.set(B.batch, batch);
        bundler.set(B.gsmState, this);
        bundler.set(B.dt, 1 / 60f);
        bundler.set(B.sr, sr);
        bundler.set(B.evol, new CrossoverEvolutionManager(new Crossover()));
    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new ChromosomeRenderSystem());
        engine.add(new ChromosomeSystem());
        engine.add(new UpdatableEntitySystem());
        engine.add(new AnimationSystem());
        engine.add(new EntityDebugSystem()
                .setTextInfoEnabled(false)
                .addHelp("R", "Restart")
                .addHelp("E", "Evolve")
                .addHelp("Y", "Keep Evolving")
                .addHelp("V", "Change view")
                .addHelp("LMB", "Select Chromosome")
        );
        engine.add(new CameraSystem());
        engine.add(new MovementSystem());
        engine.add(new TTLSystem());
        engine.add(new FunctionDrawingSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, 0).add(new CameraComponent(cam).setControllable()));
        engine.add(new Entity().add(new FunctionComponent(new ParabolaFunction(1, 2, 3))));
    }

    @Override
    protected void start() {
        engine.dispatch(new ResetEvolutionRequest());
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
            engine.dispatch(new ResetEvolutionRequest());
        } else
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)){
            engine.dispatch(new EvolveRequest());
        } else
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)){
            ChromosomeRenderSystem system = engine.getSystemManager().getSystem(ChromosomeRenderSystem.class);
            switch (system.renderMode){
                case LAST_GEN:
                case TARGET_TREE:
                    system.renderModeLastAndParents();
                    break;
                case LAST_AND_PARENTS:
                    system.renderModeLast();
                    break;
            }
        } else
        if (Gdx.input.justTouched()){
            ChromosomeRenderSystem system = engine.getSystemManager().getSystem(ChromosomeRenderSystem.class);
            if (system.chromosomesUnderMouse.size > 0){
                system.renderModeTree(system.chromosomesUnderMouse.first().id);
            } else {
                system.renderModeLastAndParents();
            }
        } else

        if (Gdx.input.isKeyPressed(Input.Keys.Y)){
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 16.666f) {
                engine.dispatch(new EvolveRequest());
            }
        }

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
        sr.setColor(Color.GREEN);
        sr.end();

        engine.render();


        batch.setProjectionMatrix(cam.combined);
        batch.begin();
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
