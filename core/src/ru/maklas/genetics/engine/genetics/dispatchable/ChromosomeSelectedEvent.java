package ru.maklas.genetics.engine.genetics.dispatchable;

import org.jetbrains.annotations.Nullable;
import ru.maklas.genetics.engine.other.Event;
import ru.maklas.mengine.Entity;

public class ChromosomeSelectedEvent implements Event {

    Entity chromosome;

    public ChromosomeSelectedEvent(Entity chromosome) {
        this.chromosome = chromosome;
    }

    @Nullable
    public Entity getChromosome() {
        return chromosome;
    }
}
