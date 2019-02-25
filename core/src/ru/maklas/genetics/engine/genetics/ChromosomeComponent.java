package ru.maklas.genetics.engine.genetics;

import ru.maklas.genetics.tests.Chromosome;
import ru.maklas.mengine.components.IRenderComponent;

/** Specifies that this Entity is chromosome **/
public class ChromosomeComponent implements IRenderComponent {

    public Chromosome chromosome;
    public boolean dirty = true;

    public ChromosomeComponent(Chromosome chromosome) {
        this.chromosome = chromosome;
    }
}
