package ru.maklas.genetics.states;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.genetics.*;
import ru.maklas.genetics.functions.GraphFunction;
import ru.maklas.genetics.functions.ParabolaFunction;
import ru.maklas.genetics.functions.bi_functions.GraphBiFunction;

public class Params {

    private GraphFunction function;
    private GraphBiFunction biFunction1;
    private GraphBiFunction biFunction2;
    private final Array<Chromosome> initialPopulation = new Array<>();
    private int bitsPerGene = 16;
    private int populationSize = 10;
    private double minValue = 0;
    private double maxValue = 1;
    private int generationMemory = 10;
    private ReproductionFunction reproductionFunction;
    private MutationFunction mutationFunction;
    private FitnessFunction fitnessFunction;
    private GenerationDistribution generationDistribution;
    private int mutationChance = 100;


    public GraphFunction getFunction() {
        return function;
    }

    public void setFunction(GraphFunction function) {
        this.function = function;
    }

    public GraphBiFunction getBiFunction1() {
        return biFunction1;
    }

    public void setBiFunction1(GraphBiFunction biFunction1) {
        this.biFunction1 = biFunction1;
    }

    public GraphBiFunction getBiFunction2() {
        return biFunction2;
    }

    public void setBiFunction2(GraphBiFunction biFunction2) {
        this.biFunction2 = biFunction2;
    }

    public Array<Chromosome> getInitialPopulation() {
        return initialPopulation;
    }

    public int getBitsPerGene() {
        return bitsPerGene;
    }

    public void setBitsPerGene(int bitsPerGene) {
        this.bitsPerGene = bitsPerGene;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
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

    public GenerationDistribution getGenerationDistribution() {
        return generationDistribution;
    }

    public void setGenerationDistribution(GenerationDistribution generationDistribution) {
        this.generationDistribution = generationDistribution;
    }

    public void setMutationChance(int mutationChance) {
        this.mutationChance = mutationChance;
    }

    /**0..100**/
    public int getMutationChance() {
        return mutationChance;
    }
}
