package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public interface FitnessFunction {


    double calculateFitness(Engine e, Entity chromosome, Array<Entity> generation);

}
