package ru.maklas.genetics.engine.genetics;

import ru.maklas.mengine.Engine;

/** Мутирует хромосому после её рождения **/
public interface MutationFunction {

    void mutate(Engine engine, Chromosome child, Chromosome parentA, Chromosome parentB);

}
