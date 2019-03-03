package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.math.CumulativeDistribution;
import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.states.Params;
import ru.maklas.genetics.tests.Chromosome;
import ru.maklas.genetics.tests.Crossover;
import ru.maklas.genetics.tests.Gene;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class CrossoverReproductionFunction implements ReproductionFunction {

    private final int crossPoints;

    public CrossoverReproductionFunction(int crossPoints) {
        this.crossPoints = crossPoints;
    }

    @Override
    public Array<Entity> reproduce(Engine engine, Params params, int generationNumber, int populationSize, Counter idCounter, Array<Entity> parents) {
        Array<Entity> nextGen = new Array<>();

        if (parents.size < 2) throw new RuntimeException("Not enough chromosomes to evolve from");

        float fitnessSumm = 0;
        FitnessFunction ff = params.getFitnessFunction();
        CumulativeDistribution<Entity> distribution = new CumulativeDistribution<>();
        for (Entity parent : parents) {
            float fitness = (float) ff.calculateFitness(engine, parent, parents);
            distribution.add(parent, fitness);
            fitnessSumm += fitness;
        }
        distribution.generate();

        while (nextGen.size < populationSize) {
            Entity a = distribution.value(Utils.rand.nextFloat() * fitnessSumm);
            Entity b = distribution.value(Utils.rand.nextFloat() * fitnessSumm);
            while (a == b){
                b = distribution.value(Utils.rand.nextFloat() * fitnessSumm);
            }


            Chromosome chromosomeA = a.get(M.chromosome).chromosome;
            Chromosome chromosomeB = b.get(M.chromosome).chromosome;
            Chromosome newChromosomeA = new Crossover().cross(chromosomeA, chromosomeB, Utils.rand.nextInt(chromosomeA.length()));
            Chromosome newChromosomeB = new Crossover().cross(chromosomeB, chromosomeA, Utils.rand.nextInt(chromosomeA.length()));



            //Random mutation part
            for (Gene gene : newChromosomeA.getGenes()) {
                int numberOfMutations = Utils.rand.nextInt(1);
                for (int j = 0; j < numberOfMutations; j++) {
                    gene.setBit(Utils.rand.nextInt(gene.length()), Utils.rand.nextBoolean());
                }
            }
            nextGen.add(EntityUtils.chromosome(idCounter.next(), newChromosomeA, generationNumber, a, b));
            if (nextGen.size < populationSize) {
                for (Gene gene : newChromosomeB.getGenes()) {
                    int numberOfMutations = Utils.rand.nextInt(1);
                    for (int j = 0; j < numberOfMutations; j++) {
                        gene.setBit(Utils.rand.nextInt(gene.length()), Utils.rand.nextBoolean());
                    }
                }
            }
            nextGen.add(EntityUtils.chromosome(idCounter.next(), newChromosomeB, generationNumber, b, a));
        }

        return nextGen;
    }
}
