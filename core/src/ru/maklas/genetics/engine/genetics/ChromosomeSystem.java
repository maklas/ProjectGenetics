package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.ImmutableArray;
import com.badlogic.gdx.utils.Queue;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.dispatchable.EvolveRequest;
import ru.maklas.genetics.engine.genetics.dispatchable.GetGenerationCommand;
import ru.maklas.genetics.engine.genetics.dispatchable.GetGenerationResult;
import ru.maklas.genetics.engine.genetics.dispatchable.ResetEvolutionRequest;
import ru.maklas.genetics.states.Params;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.tests.Gene;
import ru.maklas.genetics.tests.GeneNames;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;

public abstract class ChromosomeSystem extends EntitySystem {


    protected ImmutableArray<Entity> chromosomes;
    protected ImmutableArray<Entity> generations;
    protected Queue<Entity> generationMemory;

    protected Params params;
    protected Entity currentGeneration;
    protected int currentGenerationNumber;
    protected Counter chromosomeIdCounter = ID.counterForChromosomes();


    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        params = engine.getBundler().get(B.params);
        chromosomes = entitiesFor(ChromosomeComponent.class);
        generations = entitiesFor(GenerationComponent.class);
        generationMemory = new Queue<>();
        subscribe(EvolveRequest.class, this::evolve);
        subscribe(ResetEvolutionRequest.class, this::reset);
        subscribe(GetGenerationCommand.class, c -> dispatch(new GetGenerationResult(currentGeneration, currentGenerationNumber)));
    }

    protected abstract void evolve(EvolveRequest req);

    protected abstract void reset(ResetEvolutionRequest req);



    @Override
    public void update(float dt) {
        updateChromosomePositionsIfDirty();
    }

    protected final void updateGenerationMemoryQueue() {
        if (generationMemory.size > params.getGenerationMemory()){ //Удаляем самое старое поколение.
            Entity oldestGeneration = generationMemory.removeFirst();
            oldestGeneration.get(M.generation).chromosomes.foreach(engine::remove);
            engine.remove(oldestGeneration);
            if (generationMemory.size > 0){
                GenerationComponent oldGc = generationMemory.first().get(M.generation);
                oldGc.previousGeneration = null;
                for (Entity chromosome : oldGc.chromosomes) {
                    chromosome.get(M.parents).parents.clear();
                }
            }
        }
    }

    protected final void updateChromosomePositionsIfDirty() {
        for (Entity chromosome : chromosomes) {
            ChromosomeComponent cc = chromosome.get(M.chromosome);
            if (cc.dirty){
                cc.dirty = false;
                chromosome.x = ((float) cc.chromosome.get(GeneNames.X).decodeAsDouble());

                Gene yGene = cc.chromosome.get(GeneNames.Y);
                if (yGene != null) {
                    chromosome.y = ((float) yGene.decodeAsDouble());
                } else {
                    chromosome.y = 0;
                }
            }
        }
    }
}
