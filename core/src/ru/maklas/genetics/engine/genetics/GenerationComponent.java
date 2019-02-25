package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;

/** Tells which generation this chromosome is in **/
public class GenerationComponent implements Component {

    public int generation;
    public Entity previousGeneration;
    public final Array<Entity> chromosomes = new Array<>();

    public GenerationComponent(int generation, Entity previousGeneration) {
        this.generation = generation;
        this.previousGeneration = previousGeneration;
    }

    public GenerationComponent(int generation, Entity previousGeneration, Array<Entity> chromosomes) {
        this.generation = generation;
        this.previousGeneration = previousGeneration;
        this.chromosomes.addAll(chromosomes);
    }


}
