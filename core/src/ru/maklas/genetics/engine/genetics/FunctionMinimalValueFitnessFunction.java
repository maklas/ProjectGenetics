package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.tests.GeneNames;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

/** Формула Серова**/
public class FunctionMinimalValueFitnessFunction implements FitnessFunction {

    public double q;

    public FunctionMinimalValueFitnessFunction(double q) {
        this.q = q;
    }

    @Override
    public double calculateFitness(Engine engine, Entity chromosome, Array<Entity> generation) {
        GraphFunction function = engine.getBundler().get(B.params).getFunction();
        double cValue = function.f(chromosome.get(M.chromosome).chromosome.get(GeneNames.X).decodeAsDouble());

        int betterThanMe = generation.count(g -> g != chromosome && function.f(g.get(M.chromosome).chromosome.get(GeneNames.X).decodeAsDouble()) < cValue);

        double lowerPart = Math.pow(1d + (betterThanMe / (generation.size - 1d)), q);
        return 1d / lowerPart;
    }
}
