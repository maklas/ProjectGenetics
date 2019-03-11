package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.math.CumulativeDistribution;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.states.Params;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class CrossoverReproductionFunction implements ReproductionFunction {

    private final int crossPoints;
    private Crossover crossover;

    public CrossoverReproductionFunction(int crossPoints) {
        this.crossPoints = crossPoints;
        crossover = new Crossover();
    }

    @Override
    public Array<Entity> reproduce(Engine engine, Params params, int generationNumber, int populationSize, Counter idCounter, Array<Entity> parents) {
        Array<Entity> nextGen = new Array<>();

        if (parents.size < 2) throw new RuntimeException("Not enough chromosomes to evolve from");

        float fitnessSum = 0;
        FitnessFunction ff = params.getFitnessFunction();
        CumulativeDistribution<Entity> distribution = new CumulativeDistribution<>();
        for (Entity parent : new Array.ArrayIterable<>(parents)) {
            float fitness = (float) ff.calculateFitness(engine, parent, parents);
            distribution.add(parent, fitness);
            fitnessSum += fitness;
        }
        distribution.generate();

        while (nextGen.size < populationSize) {
            Entity a = distribution.value(Utils.rand.nextFloat() * fitnessSum);
            Entity b = distribution.value(Utils.rand.nextFloat() * fitnessSum);
            while (a == b){
                b = distribution.value(Utils.rand.nextFloat() * fitnessSum);
            }

            Chromosome chromosomeA = a.get(M.chromosome).chromosome;
            Chromosome chromosomeB = b.get(M.chromosome).chromosome;
            Crossover.Children children = crossover.cross(chromosomeA, chromosomeB, randPoints(chromosomeA.length()));

            nextGen.add(EntityUtils.chromosome(idCounter.next(), children.childA, generationNumber, a, b));
            if (nextGen.size < populationSize) {
                nextGen.add(EntityUtils.chromosome(idCounter.next(), children.childB, generationNumber, b, a));
            }
        }

        return nextGen;
    }

    private IntArray intArrayCache = new IntArray();
    private int[] randPoints(int chromosomeLength){
        if (chromosomeLength == 1){
            return new int[]{Utils.rand.nextInt(chromosomeLength)};
        }
        IntArray intArray = this.intArrayCache;
        intArray.clear();
        for (int i = 0; i < crossPoints; i++) {
            int val = Utils.rand.nextInt(chromosomeLength + 1);
            while (intArray.contains(val)){
                val = Utils.rand.nextInt(chromosomeLength + 1);
            }
            intArray.add(val);
        }

        intArray.sort();
        return intArray.toArray();
    }
}
