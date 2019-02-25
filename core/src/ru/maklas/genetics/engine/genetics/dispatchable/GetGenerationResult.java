package ru.maklas.genetics.engine.genetics.dispatchable;

import ru.maklas.genetics.engine.other.Event;
import ru.maklas.mengine.Entity;

public class GetGenerationResult implements Event {

    Entity generation;
    int generationNumber;

    public GetGenerationResult(Entity generation, int generationNumber) {
        this.generation = generation;
        this.generationNumber = generationNumber;
    }

    /** Entity with GenerationComponent **/
    public Entity getGeneration() {
        return generation;
    }

    /** Just the Generation number **/
    public int getGenerationNumber() {
        return generationNumber;
    }
}
