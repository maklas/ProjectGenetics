package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.EngineUtils;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.formulas.BiFunctionComponent;
import ru.maklas.genetics.engine.genetics.ChromosomeSystem;
import ru.maklas.genetics.engine.genetics.XyGeneChromosomeSystem;
import ru.maklas.genetics.engine.genetics.dispatchable.ChromosomeSelectedEvent;
import ru.maklas.genetics.engine.genetics.dispatchable.EvolveRequest;
import ru.maklas.genetics.engine.genetics.dispatchable.GenerationChangedEvent;
import ru.maklas.genetics.engine.genetics.dispatchable.ResetEvolutionRequest;
import ru.maklas.genetics.engine.input.EngineInputAdapter;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.*;
import ru.maklas.genetics.functions.bi_functions.SerovNashBiFunction;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.user_interface.ChromosomeInfoTable;
import ru.maklas.genetics.user_interface.ControlTable;
import ru.maklas.genetics.user_interface.CornerView;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.TestEngine;

public class BiFunGeneticsState extends AbstractEngineState {


    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private CornerView view;
    private ControlTable controlTable;
    private ChromosomeInfoTable chromosomeInfo;
    private Params params;
    private boolean keepEvolvingUI = false;


    public BiFunGeneticsState(Params params) {
        this.params = params;
    }

    @Override
    protected void loadAssets() {
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        view = new CornerView();
        controlTable = new ControlTable(true);
        chromosomeInfo = new ChromosomeInfoTable().setPrintFunctionValue(false);
    }

    @Override
    protected void fillBundler(Bundler bundler) {
        bundler.set(B.cam, cam);
        bundler.set(B.batch, batch);
        bundler.set(B.gsmState, this);
        bundler.set(B.dt, 1 / 60f);
        bundler.set(B.sr, sr);
        bundler.set(B.params, params);
    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new XyGeneChromosomeSystem());
        engine.add(new ChromosomeRenderSystem().setRenderMinMaxHorizontal(true));
        engine.add(new EntityDebugSystem()
                .setTextInfoEnabled(false)
                .addHelp("R", "Restart")
                .addHelp("E", "Evolve")
                .addHelp("U", "Evolve 60 per sec")
                .addHelp("Y", "Max evolution")
                .addHelp("LMB", "Select Chromosome")
                .addHelp("LEFT/RIGHT", "Change selected")
        );
        engine.add(new CameraSystem());
        engine.add(new TTLSystem());
        engine.add(new FunctionRenderSystem()
                .setAxisColor(Color.BLACK)
                .setNumberColor(Color.BLACK)
                .setDrawNet(false)
                .setNetColor(new Color(0.5f, 0.5f, 0.5f, 1)));
        engine.add(new FunctionTrackingRenderSystem());
        engine.add(new BiFunctionRenderSystem());
        engine.add(new GradientRenderSystem());
        //engine.add(new ParetoMinimalRenderSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, 0).add(new CameraComponent(cam).setControllable()));
        if (params.getBiFunction1() != null){
            engine.add(new Entity().add(new BiFunctionComponent(params.getBiFunction1()).setColor(new Color(0.75f, 0.23f, 0.23f, 1)).setParams(1, 1).setName("f(x)1")));
        }
        if (params.getBiFunction2() != null){
            engine.add(new Entity().add(new BiFunctionComponent(params.getBiFunction2()).setColor(new Color(0.23f, 0.23f, 0.75f, 1)).setParams(1, 1).setName("f(x)2")));
        }
    }

    @Override
    protected void start() {
        view.bottomLeft.setActor(controlTable);
        Label generationLabel = controlTable.addLabel("");
        Label bestValueLabel = controlTable.addLabel("");
        Label elitePointsLabel = controlTable.addLabel("Elite points: " + 0 + "%");
        controlTable.addLabel("Population size: " + params.getPopulationSize());
        controlTable.addLabel("Mutation chance: " + params.getMutationChance() + "%");
        controlTable.addLabel("Bits per gene: " + params.getBitsPerGene());
        controlTable.addCheckBox("Draw numbers", true, e -> engine.getSystemManager().getSystem(FunctionRenderSystem.class).setDrawAxisPortions(e));
        controlTable.addCheckBox("Draw net", false, e -> engine.getSystemManager().getSystem(FunctionRenderSystem.class).setDrawNet(e));
        controlTable.addCheckBox("Draw functions", true, e -> engine.getSystemManager().getSystem(FunctionRenderSystem.class).setDrawFunctions(e));
        controlTable.addCheckBox("Track mouse", true, e -> engine.getSystemManager().getSystem(FunctionTrackingRenderSystem.class).setEnableTracking(e));
        controlTable.addCheckBox("Print XY", true, e -> engine.getSystemManager().getSystem(FunctionTrackingRenderSystem.class).setPrintXY(e));
        controlTable.addCheckBox("Draggable camera", false, drag -> {
            ImmutableArray<Entity> cameras = engine.entitiesFor(CameraComponent.class);
            if (cameras.size() == 0) return;
            CameraComponent cc = cameras.get(0).get(M.camera);
            if (drag){
                cc.setDraggable();
            } else {
                cc.setControllable();
            }
        });
        controlTable.addCheckBox("Keep evolving", false, e -> keepEvolvingUI = e);
        controlTable.addButton("Next Generation", () -> engine.dispatch(new EvolveRequest()));
        controlTable.addButton("Reset population", () -> engine.dispatch(new ResetEvolutionRequest()));
        controlTable.addButton("Reset cam", () -> {
            engine.entitiesFor(CameraComponent.class).foreach(c -> {c.x = 0; c.y = 0;});
            cam.setPosition(0, 0);
            cam.zoom = 1;
        });
        controlTable.addButton("Criterial view", this::launchCriterialView);
        view.bottomRight.setActor(chromosomeInfo);

        engine.subscribe(GenerationChangedEvent.class, e -> generationLabel.setText("Generation: " + StringUtils.priceFormatted(e.getGenerationNumber(), '\'')));
        engine.subscribe(ChromosomeSelectedEvent.class, e -> chromosomeInfo.set(e.getChromosome()));
        engine.subscribe(GenerationChangedEvent.class, e -> bestValueLabel.setText("Best Value: " + StringUtils.dfSigDigits(e.getBestChromosome().get(M.chromosome).functionValue, 3, 3)));
        engine.subscribe(GenerationChangedEvent.class, e -> elitePointsLabel.setText("Elite points: " + StringUtils.df(EngineUtils.getElitePointsPercent(engine, 1.0) * 100, 2) + "%"));

        engine.dispatch(new ResetEvolutionRequest());
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) popState();
        if (Gdx.input.isKeyJustPressed(Input.Keys.END) && engine instanceof TestEngine) System.out.println(((TestEngine) engine).captureResults());
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
            if (params.getInitialPopulation().size == 0) {
                engine.removeAll(M.biFun);
                params.setBiFunction1(SerovNashBiFunction.rand(params.getMinValue(), params.getMaxValue()));
                params.setBiFunction2(SerovNashBiFunction.rand(params.getMinValue(), params.getMaxValue()));
                engine.add(new Entity().add(new BiFunctionComponent(params.getBiFunction1()).setColor(new Color(0.75f, 0.23f, 0.23f, 1)).setParams(1, 1).setName("f(x)1")));
                engine.add(new Entity().add(new BiFunctionComponent(params.getBiFunction2()).setColor(new Color(0.23f, 0.23f, 0.75f, 1)).setParams(1, 1).setName("f(x)2")));
            }
            engine.dispatch(new ResetEvolutionRequest());
        } else
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)){
            engine.dispatch(new EvolveRequest());
        } else
        if (Gdx.input.isKeyPressed(Input.Keys.U)){
            engine.dispatch(new EvolveRequest());
        } else
        if (Gdx.input.isKeyPressed(Input.Keys.Y) || keepEvolvingUI){
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 33.332f) {
                engine.dispatch(new EvolveRequest());
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)){
            launchCriterialView();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F) && params.getStopFunction() != null && !params.getStopFunction().shouldStop(engine)) {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 16.666f) {
                engine.dispatch(new EvolveRequest());
                if (params.getStopFunction().shouldStop(engine)){
                    break;
                }
            }
        }

        engine.update(dt);
        view.act();
    }

    private void launchCriterialView(){
        int generation = engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class).currentGenerationNumber;
        Array<Entity> chromosomes = EngineUtils.getGeneration(engine, generation).get(M.generation).chromosomes;
        pushState(new CriterialChromosomeState(params.getBiFunction1(), params.getBiFunction2(), chromosomes.map(c -> c.get(M.chromosome).chromosome), generation), false, false);
    }


    @Override
    protected InputProcessor getInput() {
        return new InputMultiplexer(view, new EngineInputAdapter(engine, cam));
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        view.resize(width, height);
    }

    @Override
    protected void render(Batch batch) {
        cam.update();

        sr.setProjectionMatrix(cam.combined);
        batch.setProjectionMatrix(cam.combined);
        engine.render();
        view.draw();
    }

    @Override
    protected void dispose() {
        super.dispose();
        sr.dispose();
        view.dispose();
    }
}
