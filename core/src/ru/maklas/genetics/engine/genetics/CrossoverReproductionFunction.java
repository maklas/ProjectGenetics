package ru.maklas.genetics.engine.genetics;

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

        if (populationSize < 2) throw new RuntimeException("Not enough chromosomes to evolve");

        for (int i = 0; i < populationSize; i++) {
            Entity a = parents.random();
            Entity b = parents.random();
            while (a == b){
                b = parents.random();
            }


            Chromosome chromosomeA = a.get(M.chromosome).chromosome;
            Chromosome chromosomeB = b.get(M.chromosome).chromosome;
            Chromosome newChromosome = new Crossover().cross(chromosomeA, chromosomeB, Utils.rand.nextInt(chromosomeA.length()));

            //Random mutation part
            for (Gene gene : newChromosome.getGenes()) {
                int numberOfMutations = Utils.rand.nextInt(1);
                for (int j = 0; j < numberOfMutations; j++) {
                    gene.setBit(Utils.rand.nextInt(gene.length()), Utils.rand.nextBoolean());
                }
            }

            nextGen.add(EntityUtils.chromosome(idCounter.next(), newChromosome, generationNumber, a, b));
        }

        return nextGen;
    }
}
