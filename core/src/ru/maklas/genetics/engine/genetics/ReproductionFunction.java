package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.states.Params;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public interface ReproductionFunction {

    Array<Entity> reproduce(Engine engine, Params params, int generationId, int populationSize, Counter idCounter, Array<Entity> parents);

}
