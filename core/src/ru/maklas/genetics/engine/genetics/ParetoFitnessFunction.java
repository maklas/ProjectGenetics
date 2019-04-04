package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.B;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.functions.bi_functions.GraphBiFunction;
import ru.maklas.genetics.states.Params;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class ParetoFitnessFunction implements FitnessFunction {

    double q;

    public ParetoFitnessFunction(double q) {
        this.q = q;
    }

    @Override
    public double calculateFitness(Engine e, Entity chromosome, Array<Entity> generation) {
        Params params = e.getBundler().get(B.params);
        GraphBiFunction f1 = params.getBiFunction1();
        GraphBiFunction f2 = params.getBiFunction2();
        Chromosome c = chromosome.get(M.chromosome).chromosome;
        double x = c.get(GeneNames.X).decodeAsDouble();
        double y = c.get(GeneNames.Y).decodeAsDouble();
        double val_1 = f1.f(x, y);
        double val_2 = f2.f(x, y);

        int better = 0;

        for (Entity entity : generation) {
            if (entity == chromosome) continue;
            Chromosome ec = entity.get(M.chromosome).chromosome;
            double eX = ec.get(GeneNames.X).decodeAsDouble();
            double eY = ec.get(GeneNames.Y).decodeAsDouble();

            if (val_1 > f1.f(eX, eY) && val_2 > f2.f(eX, eY)) { // Если у какой-то хромосомы оба значения меньше
                better++;
            }
        }

        return 1.0 / (Math.pow(1.0 + (better / (generation.size - 1.0)), q));
    }

    @Override
    public void calculateFitness(Engine e, Array<Entity> chromosomes) {
        Params params = e.getBundler().get(B.params);
        GraphBiFunction f1 = params.getBiFunction1();
        GraphBiFunction f2 = params.getBiFunction2();
        Array<Data> results = chromosomes.map(entity -> {
            ChromosomeComponent cc = entity.get(M.chromosome);
            double x = cc.chromosome.get(GeneNames.X).decodeAsDouble();
            double y = cc.chromosome.get(GeneNames.Y).decodeAsDouble();
            return new Data(cc, f1.f(x, y), f2.f(x, y));
        });

        for (int i = 0; i < results.size; i++) {
            Data a = results.get(i);
            int better = 0;
            for (int j = 0; j < results.size; j++) {
                if (j == i) continue;
                Data b = results.get(j);
                {
                    if (b.v1 < a.v1 && b.v2 < a.v2){
                        better++;
                    }
                }
            }
            a.cc.fitness = 1.0 / (Math.pow(1.0 + (better / (chromosomes.size - 1.0)), q));
        }
    }

    private static class Data {
        private final ChromosomeComponent cc;
        double v1;
        double v2;

        public Data(ChromosomeComponent cc, double v1, double v2) {
            this.cc = cc;
            this.v1 = v1;
            this.v2 = v2;
        }
    }
}
