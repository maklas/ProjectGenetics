package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.M;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class ParetoFitnessFunction implements FitnessFunction {

    @Override
    public double calculateFitness(Engine e, Entity chromosome, Array<Entity> generation) {
        return Math.pow(chromosome.get(M.chromosome).chromosome.get(GeneNames.Y).decodeAsPercent(), 3);
    }
}
