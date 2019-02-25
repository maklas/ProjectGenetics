package ru.maklas.genetics.engine.genetics.dispatchable;

import ru.maklas.genetics.engine.other.Event;
import ru.maklas.mengine.Entity;

public class GenerationChangedEvent implements Event {

    int generationNumber;
    Entity generation;

    public GenerationChangedEvent(int generationNumber, Entity generation) {
        this.generationNumber = generationNumber;
        this.generation = generation;
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public Entity getGeneration() {
        return generation;
    }
}
