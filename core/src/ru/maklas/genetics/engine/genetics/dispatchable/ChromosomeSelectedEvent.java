package ru.maklas.genetics.engine.genetics.dispatchable;

import org.jetbrains.annotations.Nullable;
import ru.maklas.genetics.engine.other.Event;
import ru.maklas.mengine.Entity;

public class ChromosomeSelectedEvent implements Event {

    Entity chromosome;
    Entity oldChromosome;

    public ChromosomeSelectedEvent(Entity oldChromosome, Entity chromosome) {
        this.oldChromosome = oldChromosome;
        this.chromosome = chromosome;
    }

    public ChromosomeSelectedEvent(Entity chromosome) {
        this.chromosome = chromosome;
    }

    @Nullable
    public Entity getChromosome() {
        return chromosome;
    }

    @Nullable
    public Entity getOldChromosome() {
        return oldChromosome;
    }
}
