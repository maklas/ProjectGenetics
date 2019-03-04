package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.M;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

import java.util.Comparator;

/** Формула Серова**/
public class FunctionMinimalValueFitnessFunction implements FitnessFunction {

    public double q;

    public FunctionMinimalValueFitnessFunction(double q) {
        this.q = q;
    }

    @Override
    public double calculateFitness(Engine engine, Entity chromosome, Array<Entity> generation) {
        double cValue = chromosome.get(M.chromosome).functionValue;

        int betterThanMe = generation.count(g -> g != chromosome && g.get(M.chromosome).functionValue < cValue);

        double lowerPart = Math.pow(1d + (betterThanMe / (generation.size - 1d)), q);
        return 1d / lowerPart;
    }

    @Override
    public void calculateFitness(Engine e, Array<Entity> chromosomes) {
        chromosomes.sort(Comparator.comparingDouble((Entity ch) -> ch.get(M.chromosome).functionValue));
        int size = chromosomes.size;
        for (int i = 0; i < size; i++) {
            double lowerPart = Math.pow(1d + (i / (size - 1d)), q);
            chromosomes.get(i).get(M.chromosome).fitness = 1d / lowerPart;
        }
    }
}
