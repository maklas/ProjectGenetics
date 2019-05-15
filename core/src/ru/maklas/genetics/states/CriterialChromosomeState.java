package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.genetics.*;
import ru.maklas.genetics.engine.genetics.dispatchable.ChromosomeSelectedEvent;
import ru.maklas.genetics.engine.input.EngineInputAdapter;
import ru.maklas.genetics.engine.input.ScrollEvent;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.CameraComponent;
import ru.maklas.genetics.engine.rendering.ChromosomeRenderSystem;
import ru.maklas.genetics.engine.rendering.FunctionRenderSystem;
import ru.maklas.genetics.functions.bi_functions.GraphBiFunction;
import ru.maklas.genetics.statics.Layers;
import ru.maklas.genetics.user_interface.ChromosomeInfoTable;
import ru.maklas.genetics.user_interface.ControlTable;
import ru.maklas.genetics.user_interface.CornerView;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.TestEngine;

public class CriterialChromosomeState extends AbstractVisualEngineState {

    private final GraphBiFunction f1;
    private final GraphBiFunction f2;
    private Array<Chromosome> chromosomes;
    private final int generation;
    private CornerView cornerView;
    private ControlTable controlTable;
    private ChromosomeInfoTable chromosomeInfoTable;

    public CriterialChromosomeState(GraphBiFunction f1, GraphBiFunction f2, Array<Chromosome> chromosomes, int generation) {
        this.f1 = f1;
        this.f2 = f2;
        this.chromosomes = chromosomes;
        this.generation = generation;
    }

    @Override
    protected void addSystems(Engine engine) {
        super.addSystems(engine);
        engine.add(new XyGeneChromosomeSystem());
        engine.add(new ChromosomeRenderSystem().setRenderMinMax(false));
        engine.add(new EntityDebugSystem()
                .setTextInfoEnabled(false)
                .addHelp("Y", "Max evolution")
                .addHelp("LMB", "Select Chromosome")
                .addHelp("LEFT/RIGHT", "Change selected")
        );
        engine.add(new TTLSystem());
        engine.add(new FunctionRenderSystem()
                .setAxisColor(Color.BLACK)
                .setNumberColor(Color.BLACK)
                .setDrawNet(false)
                .setNetColor(new Color(0.5f, 0.5f, 0.5f, 1))
                .setDrawFunctions(false)
                .setAxisNames("f1", "f2"));
    }

    @Override
    protected void loadAssets() {
        super.loadAssets();
        cornerView = new CornerView();
        controlTable = new ControlTable(true);
        chromosomeInfoTable = new ChromosomeInfoTable().setPrintFunctionValue(false);
        cornerView.bottomLeft.setActor(controlTable);
        cornerView.bottomRight.setActor(chromosomeInfoTable);
    }

    @Override
    protected void start() {
        double minX = 0d;
        double maxX = 0d;
        double minY = 0d;
        double maxY = 0d;
        Array<Entity> entities = new Array<>();
        for (Chromosome chromosome : chromosomes) {
            double cx = chromosome.get(GeneNames.X).decodeAsDouble();
            double cy = chromosome.get(GeneNames.Y).decodeAsDouble();
            float x = (float) f1.f(cx, cy);
            float y = (float) f2.f(cx, cy);
            Entity c = new Entity(1, x, y, Layers.chromosome);
            c.add(new ChromosomeComponent(chromosome, generation, false));
            entities.add(c);
            engine.add(c);
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        Entity generation = new Entity().add(new GenerationComponent(this.generation, null, entities));
        engine.add(generation);

        engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class).currentGeneration = generation;
        engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class).currentGenerationNumber = this.generation;

        if (maxX > minX && maxY > minY) {
            int width = Gdx.graphics.getWidth();
            int height = Gdx.graphics.getHeight();
            double nWidth = maxX - minX;
            double nHeight = maxY - minY;

            double diff = Math.max(nWidth / width, nHeight / height);
            int zooms = (int) Math.ceil(Math.log(diff) / Math.log(2));

            for (int i = 0; i < zooms; i++) {
                engine.dispatch(new ScrollEvent(false));
            }
            engine.entitiesFor(CameraComponent.class).first().set((float)(((maxX - minX) / 2) + minX), (float) (((maxY - minY) / 2) + minY));
        }

        controlTable.addButton("Back", this::popState);
        engine.subscribe(ChromosomeSelectedEvent.class, e -> chromosomeInfoTable.set(e.getChromosome()));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        cornerView.resize(width, height);
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            popState();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.END) && engine instanceof TestEngine) {
            System.out.println(((TestEngine) engine).captureResults());
        }
        engine.update(dt);
        cornerView.act();
    }

    @Override
    protected InputProcessor getInput() {
        return new InputMultiplexer(cornerView, new EngineInputAdapter(engine, cam));
    }

    @Override
    protected void render(Batch batch) {
        cam.update();

        sr.setProjectionMatrix(cam.combined);
        batch.setProjectionMatrix(cam.combined);
        engine.render();
        cornerView.draw();
    }
}
