package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.*;
import ru.maklas.genetics.engine.other.EntityDebugSystem;
import ru.maklas.genetics.engine.other.MovementSystem;
import ru.maklas.genetics.engine.other.TTLSystem;
import ru.maklas.genetics.engine.rendering.AnimationSystem;
import ru.maklas.genetics.engine.rendering.CameraComponent;
import ru.maklas.genetics.engine.rendering.CameraSystem;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.statics.Layers;
import ru.maklas.genetics.tests.Chromosome;
import ru.maklas.genetics.tests.Crossover;
import ru.maklas.genetics.tests.Gene;
import ru.maklas.genetics.tests.GeneNames;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.UpdatableEntitySystem;

public class GeneticsGenerationState extends AbstractEngineState {

    private static final int generationSize = 30;
    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private Counter chromosomeIdCounter = new Counter(100_000, 2_000_000_000);
    private int currentGeneration = 0;

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
    }

    @Override
    protected void addSystems(Engine engine) {
        engine.add(new ChromosomeRenderSystem());
        engine.add(new ChromosomeSystem());
        engine.add(new UpdatableEntitySystem());
        engine.add(new AnimationSystem());
        engine.add(new EntityDebugSystem().setTextInfoEnabled(false));
        engine.add(new CameraSystem());
        engine.add(new MovementSystem());
        engine.add(new TTLSystem());
    }

    @Override
    protected void addDefaultEntities(Engine engine) {
        engine.add(new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, 0).add(new CameraComponent(cam).setControllable()));
        randomizeChromosomes();
    }

    private void randomizeChromosomes(){
        currentGeneration = 0;
        ImmutableArray<Entity> chromosomes = engine.entitiesFor(ChromosomeComponent.class);
        chromosomes.foreach(engine::removeLater);

        for (int i = 0; i < generationSize; i++) {
            Chromosome chromosome = new Chromosome()
                    .add(new Gene(16).setName(GeneNames.X).setMinMaxDouble(0, 1000).randomize())
                    .add(new Gene(16).setName(GeneNames.Y).setMinMaxDouble(0, 1000).randomize());

            Entity e = new Entity(chromosomeIdCounter.next(), EntityType.CHROMOSOME, 0, 0, Layers.chromosome);
            e.add(new ChromosomeComponent(chromosome));
            e.add(new GenerationComponent(currentGeneration));
            e.add(new ParentsComponent());
            engine.add(e);
        }
        engine.getSystemManager().getSystem(ChromosomeRenderSystem.class).currentGeneration = currentGeneration;
    }

    private void evolve(){
        Array<Entity> lastGenerationChromosomes = engine.entitiesFor(ChromosomeComponent.class).mapReduce(e -> {
            if (e.get(M.generation).generation == currentGeneration){
                return e;
            } else {
                return null;
            }
        });

        final int newGenerationSize = generationSize;

        for (int i = 0; i < newGenerationSize; i++) {
            Entity a = lastGenerationChromosomes.random();
            Entity b = lastGenerationChromosomes.random();
            while (b == a){
                b = lastGenerationChromosomes.random();
            }

            Chromosome newChromosome = new Crossover().cross(a.get(M.chromosome).chromosome, b.get(M.chromosome).chromosome, Utils.rand.nextInt(8 * 8));
            Entity child = new Entity(chromosomeIdCounter.next(), EntityType.CHROMOSOME, 0, 0, Layers.chromosome);
            child.add(new ChromosomeComponent(newChromosome));
            child.add(new GenerationComponent(currentGeneration + 1));
            child.add(new ParentsComponent().add(a).add(b));
            engine.add(child);
        }

        currentGeneration++;
        engine.getSystemManager().getSystem(ChromosomeRenderSystem.class).currentGeneration = currentGeneration;
    }

    @Override
    protected void start() { }

    @Override
    protected void update(float dt) {
        engine.update(dt);
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)){
            evolve();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
            randomizeChromosomes();
        }
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
