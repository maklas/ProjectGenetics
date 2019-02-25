package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.statics.EntityType;
import ru.maklas.genetics.statics.Layers;
import ru.maklas.genetics.tests.Chromosome;
import ru.maklas.mengine.Entity;

public class EntityUtils {

    public static Entity chromosome(int id, Chromosome chromosome, int generation){
        return new Entity(id, EntityType.CHROMOSOME, 0, 0, Layers.chromosome)
                .add(new ChromosomeComponent(chromosome, generation))
                .add(new ParentsComponent());
    }

    public static Entity chromosome(int id, Chromosome chromosome, int generation, Entity... parents){
        return new Entity(id, EntityType.CHROMOSOME, 0, 0, Layers.chromosome)
                .add(new ChromosomeComponent(chromosome, generation))
                .add(new ParentsComponent(parents));
    }

    public static Entity generation(int id, int generation, Entity previousGeneration, Array<Entity> chromosomes){
        return new Entity(id, EntityType.GENERATION, 0, 0, Layers.generation)
                .add(new GenerationComponent(generation, previousGeneration, chromosomes));
    }


}
