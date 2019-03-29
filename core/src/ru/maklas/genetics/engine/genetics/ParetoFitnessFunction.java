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
            if (val_1 > f1.f(eX, y) && val_2 > f2.f(x, eY)){ //Если оба параметра нашей хромосомы лучше соседа
                better++;
            }
        }

        return 1.0 / (Math.pow(1.0 + (better / (generation.size - 1.0)), q));
    }
}
