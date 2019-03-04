package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.M;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public interface FitnessFunction {

    /**
     * Использовать только если {@link ChromosomeComponent#functionValue} выставлено
     * @param chromosome Хромосома, для которй вычислить фитнесс
     * @param generation Поколение данной хромосомы. Может включать в себя саму хромосому
     */
    double calculateFitness(Engine e, Entity chromosome, Array<Entity> generation);

    /**
     * Использовать только если {@link ChromosomeComponent#functionValue} выставлено
     * Подсчитывает фитнесс функцию для вскх хромосом и кладёт её в {@link ChromosomeComponent#fitness}
     * Можно сортировать массив для оптимизации.
     */
    default void calculateFitness(Engine e, Array<Entity> chromosomes){
        for (Entity chromosome : chromosomes) {
            chromosome.get(M.chromosome).fitness = calculateFitness(e, chromosome, chromosomes);
        }
    }

}
