package ru.maklas.genetics.states;

import ru.maklas.genetics.engine.genetics.FitnessFunction;
import ru.maklas.genetics.engine.genetics.MutationFunction;
import ru.maklas.genetics.engine.genetics.ReproductionFunction;
import ru.maklas.genetics.utils.functions.GraphFunction;
import ru.maklas.genetics.utils.functions.LinearFunction;

public class Params {

    private GraphFunction function = new LinearFunction(1, 0);
    private int bitsPerGene = 16;
    private int chromosomesPerGeneration = 10;
    private double minValue = 0;
    private double maxValue = 1;
    private int generationMemory = 10;
    private ReproductionFunction reproductionFunction;
    private MutationFunction mutationFunction;
    private FitnessFunction fitnessFunction;


    public GraphFunction getFunction() {
        return function;
    }

    public void setFunction(GraphFunction function) {
        this.function = function;
    }

    public int getBitsPerGene() {
        return bitsPerGene;
    }

    public void setBitsPerGene(int bitsPerGene) {
        this.bitsPerGene = bitsPerGene;
    }

    public int getChromosomesPerGeneration() {
        return chromosomesPerGeneration;
    }

    public void setChromosomesPerGeneration(int chromosomesPerGeneration) {
        this.chromosomesPerGeneration = chromosomesPerGeneration;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinMax(double min, double max){
        setMinValue(min);
        setMaxValue(max);
    }

    public int getGenerationMemory() {
        return generationMemory;
    }

    public void setGenerationMemory(int generationMemory) {
        this.generationMemory = generationMemory;
    }

    public ReproductionFunction getReproductionFunction() {
        return reproductionFunction;
    }

    public void setReproductionFunction(ReproductionFunction reproductionFunction) {
        this.reproductionFunction = reproductionFunction;
    }

    public MutationFunction getMutationFunction() {
        return mutationFunction;
    }

    public void setMutationFunction(MutationFunction mutationFunction) {
        this.mutationFunction = mutationFunction;
    }

    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }
}
