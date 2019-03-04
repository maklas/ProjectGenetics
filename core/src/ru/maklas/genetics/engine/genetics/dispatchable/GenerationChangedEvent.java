package ru.maklas.genetics.engine.genetics.dispatchable;

import ru.maklas.genetics.engine.other.Event;
import ru.maklas.mengine.Entity;

public class GenerationChangedEvent implements Event {

    int generationNumber;
    Entity generation;
    Entity bestChromosome;

    public GenerationChangedEvent(int generationNumber, Entity generation, Entity bestChromosome) {
        this.generationNumber = generationNumber;
        this.generation = generation;
        this.bestChromosome = bestChromosome;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public Entity getGeneration() {
        return generation;
    }

    public Entity getBestChromosome() {
        return bestChromosome;
    }
}
