package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import com.badlogic.gdx.utils.Queue;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.dispatchable.*;
import ru.maklas.genetics.engine.input.TouchDownEvent;
import ru.maklas.genetics.states.Params;
import ru.maklas.genetics.statics.ID;
import ru.maklas.genetics.tests.Gene;
import ru.maklas.genetics.tests.GeneNames;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;

public abstract class ChromosomeSystem extends EntitySystem {

    protected ImmutableArray<Entity> chromosomes;
    protected ImmutableArray<Entity> generations;
    protected Queue<Entity> generationMemory;

    protected Params params;
    protected OrthographicCamera cam;
    public Entity currentGeneration;
    public int currentGenerationNumber;
    protected Counter chromosomeIdCounter = ID.counterForChromosomes();

    public Entity selectedChromosome;
    public Entity bestChromosomeOfGeneration;
    public Array<Entity> chromosomesUnderMouse = new Array<>();
    public float collectionRange = 7;
    public float parentCollectionRange = 10;


    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        params = engine.getBundler().get(B.params);
        cam = engine.getBundler().get(B.cam);
        chromosomes = entitiesFor(ChromosomeComponent.class);
        generations = entitiesFor(GenerationComponent.class);
        generationMemory = new Queue<>();
        subscribe(EvolveRequest.class, this::evolve);
        subscribe(ResetEvolutionRequest.class, this::reset);
        subscribe(GetGenerationCommand.class, c -> dispatch(new GetGenerationResult(currentGeneration, currentGenerationNumber)));
        subscribe(TouchDownEvent.class, this::onTouchDown);
        subscribe(GenerationChangedEvent.class, this::generationChanged);
    }

    private void generationChanged(GenerationChangedEvent e) {
        if (selectedChromosome != null) {
            selectedChromosome = null;
            dispatch(new ChromosomeSelectedEvent(null));
        }
    }

    protected abstract void evolve(EvolveRequest req);

    protected abstract void reset(ResetEvolutionRequest req);

    protected void onTouchDown(TouchDownEvent e) {
        Array<Entity> chromosomesUnderMouse = getChromosomesUnderMouse(new Array<>(), e.toVector(), selectedChromosome == null ? -1 : selectedChromosome.id, currentGenerationNumber);
        if (selectedChromosome != null) {
            if (chromosomesUnderMouse.contains(selectedChromosome, true)){
                return;
            } else {
                ParentsComponent pc = selectedChromosome.get(M.parents);
                Entity selectedParent = null;
                if (pc != null && pc.parents.size > 0){
                    for (Entity parent : pc.parents) {
                        if (chromosomesUnderMouse.contains(parent, true)){
                            selectedParent = parent;
                            break;
                        }
                    }
                }
                selectedChromosome = selectedParent;
                dispatch(new ChromosomeSelectedEvent(selectedParent));

            }
        } else {
            selectedChromosome = chromosomesUnderMouse.size == 0 ? null : chromosomesUnderMouse.get(0);
            dispatch(new ChromosomeSelectedEvent(selectedChromosome));
        }
    }

    @Override
    public void update(float dt) {
        if (selectedChromosome != null && !selectedChromosome.isInEngine()) {
            selectedChromosome = null;
            dispatch(new ChromosomeSelectedEvent(null));
        }
        updateChromosomesUnderMouse();
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

    protected final void updateChromosomesPositionsIfDirty(Array<Entity> chromosomes) {
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

    protected final Entity selectBestChromosomeOfGeneration(Array<Entity> chromosomes){
        Entity best = chromosomes.get(0);
        double bestFitness = best.get(M.chromosome).fitness;
        for (Entity chromosome : chromosomes) {
            double f = chromosome.get(M.chromosome).fitness;
            if (f > bestFitness){
                bestFitness = f;
                best = chromosome;
            }
        }
        return best;
    }

    protected final void updateChromosomesFunctionValue(GraphFunction function, Array<Entity> chromosomes){
        for (Entity chromosome : chromosomes) {
            ChromosomeComponent cc = chromosome.get(M.chromosome);
            cc.functionValue = function.f(cc.chromosome.get(GeneNames.X).decodeAsDouble());
        }
    }

    protected final void updateChromosomesUnderMouse(){
        chromosomesUnderMouse.clear();
        getChromosomesUnderMouse(chromosomesUnderMouse, selectedChromosome == null ? -1: selectedChromosome.id, currentGenerationNumber);
    }

    /** Если targetId >= 0, то проверяем попадает ли этот Entity под мышку, иначе ищем среди всех указанных поколений. **/
    public Array<Entity> getChromosomesUnderMouse(Array<Entity> entities, int targetId, int... generations) {
        return getChromosomesUnderMouse(entities, Utils.getMouse(cam), targetId, generations);
    }

    /** Если targetId >= 0, то проверяем попадает ли этот Entity под мышку, иначе ищем среди всех указанных поколений. **/
    public Array<Entity> getChromosomesUnderMouse(Array<Entity> entities, Vector2 mouse, int targetId, int... generations){
        final float range = collectionRange * cam.zoom;
        final float range2 = range * range;

        if (currentGeneration == null) return entities;

        if (targetId >= 0){
            Entity target = engine.findById(targetId);
            if (target != null && mouse.dst2(target.x, target.y) < range2){
                entities.add(target);
            }
            ParentsComponent pc = target.get(M.parents);
            if (pc != null){
                for (Entity parent : pc.parents) {
                    if (mouse.dst2(parent.x, parent.y) < parentCollectionRange * parentCollectionRange * cam.zoom * cam.zoom){
                        entities.add(parent);
                    }
                }

            }
        }  else {

            for (Entity generation : this.generations) {
                if (Utils.contains(generations, generation.get(M.generation).generation)){
                    for (Entity chromosome : generation.get(M.generation).chromosomes) {

                        if (mouse.dst2(chromosome.x, chromosome.y) < range2){
                            entities.add(chromosome);
                        }
                    }

                }
            }

        }

        entities.sort((a, b) -> Float.compare(mouse.dst2(a.x, a.y), mouse.dst2(b.x, b.y)));
        return entities;
    }



}
