package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import com.badlogic.gdx.utils.Queue;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.dispatchable.*;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.tests.Chromosome;
import ru.maklas.genetics.tests.EvolutionManager;
import ru.maklas.genetics.tests.Gene;
import ru.maklas.genetics.tests.GeneNames;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;

public class ChromosomeSystem extends EntitySystem {

    private static final int chromosomesPerGeneration = 100;
    private static final int maxGenerationMemory = 100;

    private ImmutableArray<Entity> chromosomes;
    private ImmutableArray<Entity> generations;
    private Queue<Entity> generationMemory;

    private Entity currentGeneration;
    private int currentGenerationNumber;
    private Counter chromosomeIdCounter = ID.counterForChromosomes();
    private EvolutionManager evolutionManager;


    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        evolutionManager = engine.getBundler().getAssert(B.evol);
        chromosomes = entitiesFor(ChromosomeComponent.class);
        generations = entitiesFor(GenerationComponent.class);
        generationMemory = new Queue<>();
        subscribe(EvolveRequest.class, this::evolve);
        subscribe(ResetEvolutionRequest.class, this::reset);
        subscribe(GetGenerationCommand.class, c -> dispatch(new GetGenerationResult(currentGeneration, currentGenerationNumber)));
    }

    private void evolve(EvolveRequest req) {
        if (generationMemory.size == 0) { //Если старого поколения нет -> создаём
            dispatch(new ResetEvolutionRequest());
            return;
        }

        //Создаём следующее поколение из прошлого
        Entity currGen = generationMemory.last();
        GenerationComponent gc = currGen.get(M.generation);
        Array<Entity> newChromosomes = evolutionManager.evolve(engine, currentGenerationNumber + 1, chromosomeIdCounter, gc.chromosomes);
        engine.addAll(newChromosomes);
        currentGenerationNumber++;
        currentGeneration = EntityUtils.generation(1000 + currentGenerationNumber, currentGenerationNumber, currGen, newChromosomes);
        engine.add(currentGeneration);
        generationMemory.addLast(currentGeneration);


        if (generationMemory.size > maxGenerationMemory){ //Удаляем самое старое поколение.
            Entity oldestGeneration = generationMemory.removeFirst();
            oldestGeneration.get(M.generation).chromosomes.foreach(engine::removeLater);
            engine.remove(oldestGeneration);
            if (generationMemory.size > 0){
                GenerationComponent oldGc = generationMemory.first().get(M.generation);
                oldGc.previousGeneration = null;
                for (Entity chromosome : oldGc.chromosomes) {
                    chromosome.get(M.parents).parents.clear();
                }

            }
        }

        dispatch(new GenerationChangedEvent(currentGenerationNumber, currentGeneration));
    }

    private void reset(ResetEvolutionRequest req) {
        generations.cpyArray().foreach(engine::remove);
        chromosomes.cpyArray().foreach(engine::remove);


        generationMemory.clear();
        currentGenerationNumber = 0;
        Array<Entity> newChromosomes = new Array<>();

        for (int i = 0; i < chromosomesPerGeneration; i++) {
            Chromosome chromosome = new Chromosome();
            chromosome.add(new Gene(4 * 8).setName(GeneNames.X).randomize().setMinMaxDouble(100, 1000));
            chromosome.add(new Gene(4 * 8).setName(GeneNames.Y).randomize().setMinMaxDouble(100, 1000));
            Entity e = EntityUtils.chromosome(chromosomeIdCounter.next(), chromosome, currentGenerationNumber);
            newChromosomes.add(e);
            engine.add(e);
        }

        currentGeneration = EntityUtils.generation(1000 + currentGenerationNumber, currentGenerationNumber, null, newChromosomes);
        engine.add(currentGeneration);
        generationMemory.addLast(currentGeneration);
        dispatch(new GenerationChangedEvent(currentGenerationNumber, currentGeneration));
    }

    @Override
    public void update(float dt) {
        for (Entity chromosome : chromosomes) {
            ChromosomeComponent cc = chromosome.get(M.chromosome);
            if (cc.dirty){
                cc.dirty = false;
                chromosome.x = ((float) cc.chromosome.get(GeneNames.X).decodeAsDouble());
                chromosome.y = ((float) cc.chromosome.get(GeneNames.Y).decodeAsDouble());
            }
        }
    }
}
