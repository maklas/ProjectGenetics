package ru.maklas.genetics.tests;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.EntityUtils;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class CrossoverEvolutionManager extends EvolutionManager {

    Crossover crossover;

    public CrossoverEvolutionManager(Crossover crossover) {
        this.crossover = crossover;
    }


    @Override
    public Array<Entity> evolve(Engine engine, int generationNumber, int generationSize, Counter idCounter, Array<Entity> chromosomes) {
        Array<Entity> nextGen = new Array<>();

        if (chromosomes.size < 2) throw new RuntimeException("Not enough chromosomes to evolve");

        for (int i = 0; i < generationSize; i++) {
            Entity a = chromosomes.random();
            Entity b = chromosomes.random();
            while (a == b){
                b = chromosomes.random();
            }


            Chromosome chromosomeA = a.get(M.chromosome).chromosome;
            Chromosome chromosomeB = b.get(M.chromosome).chromosome;
            Chromosome newChromosome = new Crossover().cross(chromosomeA, chromosomeB, Utils.rand.nextInt(chromosomeA.length()));
            nextGen.add(EntityUtils.chromosome(idCounter.next(), newChromosome, generationNumber, a, b));
        }

        return nextGen;
    }
}
