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

        int better1 = 0;
        int better2 = 0;

        for (Entity entity : generation) {
            if (entity == chromosome) continue;
            Chromosome ec = entity.get(M.chromosome).chromosome;
            if (f1.f(ec.get(GeneNames.X).decodeAsDouble(), y) < val_1){
                better1++;
            }
            if (f2.f(x, ec.get(GeneNames.Y).decodeAsDouble()) < val_2) {
                better2++;
            }
        }

        return 1.0 / (Math.pow(1.0 + (Math.min(better1, better2) / (generation.size - 1.0)), q));
    }
}
