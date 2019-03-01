package ru.maklas.genetics.engine.genetics;

import ru.maklas.genetics.tests.Chromosome;
import ru.maklas.mengine.components.IRenderComponent;

/** Specifies that this Entity is chromosome **/
public class ChromosomeComponent implements IRenderComponent {

    public Chromosome chromosome;
    public int generation;
    public boolean dirty = true; //Если да, то позиция будет обновлена в следующем кадре

    public ChromosomeComponent(Chromosome chromosome, int generation) {
        this.chromosome = chromosome;
        this.generation = generation;
    }
}
