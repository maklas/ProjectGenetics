package ru.maklas.genetics.engine.genetics;

import ru.maklas.mengine.components.IRenderComponent;

/** Specifies that this Entity is chromosome **/
public class ChromosomeComponent implements IRenderComponent {

    public Chromosome chromosome;
    public int generation;
    public boolean dirty = true; //Если да, то позиция и functionValue будет обновлена в следующем кадре
    public double functionValue = 0; //Значение хромосомы по функции.
    public double fitness = 0;

    public ChromosomeComponent(Chromosome chromosome, int generation) {
        this.chromosome = chromosome;
        this.generation = generation;
    }

    public ChromosomeComponent(Chromosome chromosome, int generation, boolean dirty) {
        this.chromosome = chromosome;
        this.generation = generation;
        this.dirty = dirty;
    }
}
