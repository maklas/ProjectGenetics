package ru.maklas.genetics.tests;

import com.badlogic.gdx.utils.Array;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public abstract class EvolutionManager {


    public final Array<Entity> evolve(Engine engine, int generationNumber, Counter idCounter, Array<Entity> chromosomes){
        return evolve(engine, generationNumber, chromosomes.size, idCounter, chromosomes);
    }

    public abstract Array<Entity> evolve(Engine engine, int generationNumber, int generationSize, Counter idCounter, Array<Entity> chromosomes);

}
