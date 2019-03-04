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
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.formulas.FunctionComponent;
import ru.maklas.genetics.engine.formulas.FunctionRenderSystem;
import ru.maklas.genetics.engine.formulas.FunctionTrackingRenderSystem;
import ru.maklas.genetics.engine.genetics.ChromosomeRenderSystem;
import ru.maklas.genetics.engine.genetics.ChromosomeTrackMode;
import ru.maklas.genetics.engine.genetics.ChromosomeTrackingRenderSystem;
import ru.maklas.genetics.engine.genetics.XGeneChromosomeSystem;
import ru.maklas.genetics.engine.genetics.dispatchable.ChromosomeSelectedEvent;
import ru.maklas.genetics.engine.genetics.dispatchable.EvolveRequest;
import ru.maklas.genetics.engine.genetics.dispatchable.GenerationChangedEvent;
import ru.maklas.genetics.engine.genetics.dispatchable.ResetEvolutionRequest;
import ru.maklas.genetics.engine.input.EngineInputAdapter;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.CameraComponent;
import ru.maklas.genetics.engine.rendering.CameraSystem;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.user_interface.ChromosomeInfoTable;
import ru.maklas.genetics.user_interface.ControlTable;
import ru.maklas.genetics.user_interface.CornerView;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.genetics.utils.functions.FunctionFromPoints;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.TestEngine;

public class GeneticsGenerationState extends AbstractEngineState {

    private final Params params;
    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private CornerView view;
    private ControlTable controlTable;
    private ChromosomeInfoTable chromosomeInfo;
    private FunctionFromPoints functionFromPoints;

    public GeneticsGenerationState(Params params) {
        this.params = params;
    }

    @Override
    protected void loadAssets() {
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        view = new CornerView();
        controlTable = new ControlTable(true);
        chromosomeInfo = new ChromosomeInfoTable();
        functionFromPoints = new FunctionFromPoints();
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
        engine.add(new XGeneChromosomeSystem());
        engine.add(new ChromosomeRenderSystem());
        engine.add(new EntityDebugSystem()
                .setTextInfoEnabled(false)
                .addHelp("R", "Restart")
                .addHelp("E", "Evolve")
                .addHelp("U", "Evolve 60 per sec")
                .addHelp("Y", "Max evolution")
                .addHelp("LMB", "Select Chromosome")
        );
        engine.add(new CameraSystem());
        engine.add(new TTLSystem());
        engine.add(new FunctionRenderSystem()
                .setNetColor(Color.BLACK)
                .setNumberColor(Color.BLACK)
                .setFillNet(false)
                .setFillColor(new Color(0.5f, 0.5f, 0.5f, 1)));
        engine.add(new FunctionTrackingRenderSystem());
        engine.add(new ChromosomeTrackingRenderSystem().setMode(ChromosomeTrackMode.CURRENT_GEN));

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
        view.bottomLeft.setActor(controlTable);
        Label generationLabel = controlTable.addLabel("");
        Label bestValueLabel = controlTable.addLabel("");
        controlTable.addCheckBox("Draw numbers", true, e -> engine.getSystemManager().getSystem(FunctionRenderSystem.class).setDrawPortions(e));
        controlTable.addCheckBox("Draw net", false, e -> engine.getSystemManager().getSystem(FunctionRenderSystem.class).setFillNet(e));
        controlTable.addCheckBox("Draw functions", true, e -> engine.getSystemManager().getSystem(FunctionRenderSystem.class).setDrawFunctions(e));
        controlTable.addCheckBox("Track mouse", true, e -> engine.getSystemManager().getSystem(FunctionTrackingRenderSystem.class).setEnableTracking(e));
        ChromosomeTrackingRenderSystem system = engine.getSystemManager().getSystem(ChromosomeTrackingRenderSystem.class);
        controlTable.addButton(system.trackMode.asText(), (b) -> {
            system.trackMode = Utils.next(system.trackMode);
            b.setText(system.trackMode.asText());
        });
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
        controlTable.addButton("Next Generation", () -> engine.dispatch(new EvolveRequest()));
        controlTable.addButton("Reset population", () -> engine.dispatch(new ResetEvolutionRequest()));
        controlTable.addButton("Reset cam", () -> {
            engine.entitiesFor(CameraComponent.class).foreach(c -> {c.x = 0; c.y = 0;});
            cam.setPosition(0, 0);
            cam.zoom = 1;
        });
        controlTable.addButton("History", () -> pushState(new HistoryState(functionFromPoints)));

        view.bottomRight.setActor(chromosomeInfo);

        engine.subscribe(GenerationChangedEvent.class, e -> generationLabel.setText("Generation: " + StringUtils.priceFormatted(e.getGenerationNumber(), '\'')));
        engine.subscribe(ChromosomeSelectedEvent.class, e -> chromosomeInfo.set(e.getChromosome()));
        engine.subscribe(GenerationChangedEvent.class, e -> bestValueLabel.setText("Best Value: " + StringUtils.dfSigDigits(e.getBestChromosome().get(M.chromosome).functionValue, 3, 3)));
        engine.subscribe(GenerationChangedEvent.class, e -> {
            if (e.getGenerationNumber() == 0){
                functionFromPoints.clear();
            }
            functionFromPoints.add(((float) e.getBestChromosome().get(M.chromosome).functionValue));
        });

        engine.dispatch(new ResetEvolutionRequest());
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) popState();
        if (Gdx.input.isKeyJustPressed(Input.Keys.END) && engine instanceof TestEngine) System.out.println(((TestEngine) engine).captureResults());
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
            engine.dispatch(new ResetEvolutionRequest());
        } else
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)){
            engine.dispatch(new EvolveRequest());
        } else
        if (Gdx.input.isKeyPressed(Input.Keys.U)){
            engine.dispatch(new EvolveRequest());
        } else
        if (Gdx.input.isKeyPressed(Input.Keys.Y)){
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 16.666f) {
                engine.dispatch(new EvolveRequest());
            }
        }

        engine.update(dt);
        view.act();
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
