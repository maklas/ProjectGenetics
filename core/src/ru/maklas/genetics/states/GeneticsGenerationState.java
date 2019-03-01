package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.formulas.FunctionComponent;
import ru.maklas.genetics.engine.formulas.FunctionRenderSystem;
import ru.maklas.genetics.engine.formulas.FunctionTrackingRenderSystem;
import ru.maklas.genetics.engine.genetics.ChromosomeRenderSystem;
import ru.maklas.genetics.engine.genetics.ChromosomeSystem;
import ru.maklas.genetics.engine.genetics.dispatchable.EvolveRequest;
import ru.maklas.genetics.engine.genetics.dispatchable.ResetEvolutionRequest;
import ru.maklas.genetics.engine.input.EngineInputAdapter;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.MovementSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.AnimationSystem;
import ru.maklas.genetics.engine.rendering.CameraComponent;
import ru.maklas.genetics.engine.rendering.CameraSystem;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.tests.Crossover;
import ru.maklas.genetics.tests.CrossoverEvolutionManager;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntitySystem;

public class GeneticsGenerationState extends AbstractEngineState {

    private final Params params;
    private ShapeRenderer sr;
    private OrthographicCamera cam;

    public GeneticsGenerationState(Params params) {
        this.params = params;
    }

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
        bundler.set(B.params, params);
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
        engine.add(new FunctionRenderSystem()
                .setNetColor(Color.BLACK)
                .setNumberColor(Color.BLACK)
                .setFillColor(new Color(0.5f, 0.5f, 0.5f, 1)));
        engine.add(new FunctionTrackingRenderSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, 0).add(new CameraComponent(cam).setControllable()));
        FunctionComponent fc = new FunctionComponent(params.getFunction());
        fc.color.set(0.75f, 0.23f, 0.23f, 1);
        fc.lineWidth = 1.5f;
        engine.add(new Entity().add(fc));
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
    protected InputProcessor getInput() {
        return new InputMultiplexer(new EngineInputAdapter(engine, cam));
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
        sr.begin(ShapeRenderer.ShapeType.Line);

        sr.setColor(Color.CYAN);
        sr.setColor(Color.GREEN);
        sr.end();

        engine.render();


        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.end();
    }


    @Override
    protected void dispose() {
        super.dispose();
        sr.dispose();
    }
}
