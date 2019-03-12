package ru.maklas.genetics.engine.genetics;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.M;
import ru.maklas.genetics.engine.genetics.dispatchable.EvolveRequest;
import ru.maklas.genetics.engine.genetics.dispatchable.GenerationChangedEvent;
import ru.maklas.genetics.engine.genetics.dispatchable.ResetEvolutionRequest;
import ru.maklas.genetics.utils.Utils;
import ru.maklas.mengine.Entity;

public class XGeneChromosomeSystem extends ChromosomeSystem {

    @Override
    protected void evolve(EvolveRequest req) {
        if (generationMemory.size == 0) { //Если старого поколения нет -> создаём
            dispatch(new ResetEvolutionRequest());
            return;
        }

        //Создаём следующее поколение из прошлого
        Entity currGen = generationMemory.last();
        GenerationComponent gc = currGen.get(M.generation);
        Array<Entity> newChromosomes = params.getReproductionFunction().reproduce(engine, params, currentGenerationNumber + 1, params.getPopulationSize(), chromosomeIdCounter, gc.chromosomes);
        if (Utils.rand.nextInt(100) < params.getMutationChance()) {
            MutationFunction mutationFunction = params.getMutationFunction();
            newChromosomes.foreach(c -> {
                ParentsComponent pc = c.get(M.parents);
                mutationFunction.mutate(engine, c.get(M.chromosome).chromosome, pc.parents.get(0).get(M.chromosome).chromosome, pc.parents.get(1).get(M.chromosome).chromosome);
            });
        }
        engine.addAll(newChromosomes);
        currentGenerationNumber++;
        currentGeneration = EntityUtils.generation(1000 + currentGenerationNumber, currentGenerationNumber, currGen, newChromosomes);
        engine.add(currentGeneration);
        generationMemory.addLast(currentGeneration);

        updateGenerationMemoryQueue();

        updateChromosomesPositionsIfDirty(newChromosomes);
        if (params.getFunction() != null) {
            updateChromosomesFunctionValue(params.getFunction(), newChromosomes);
        }
        if (params.getFitnessFunction() != null){
            params.getFitnessFunction().calculateFitness(engine, newChromosomes);
        }
        bestChromosomeOfGeneration = selectBestChromosomeOfGeneration(newChromosomes);
        dispatch(new GenerationChangedEvent(currentGenerationNumber, currentGeneration, bestChromosomeOfGeneration));
    }

    @Override
    protected void reset(ResetEvolutionRequest req) {
        engine.removeAll(M.generation);
        engine.removeAll(M.chromosome);

        generationMemory.clear();
        currentGenerationNumber = 0;
        Array<Entity> newChromosomes = new Array<>();


        if (params.getInitialPopulation().isEmpty()) {

            for (int i = 0; i < params.getPopulationSize(); i++) {
                Chromosome chromosome = new Chromosome();
                Gene gene = new Gene(params.getBitsPerGene())
                        .setName(GeneNames.X)
                        .setMinMaxDouble(params.getMinValue(), params.getMaxValue());
                if (params.getGenerationDistribution() == GenerationDistribution.EVEN){
                    gene.set(params.getMinValue() + ((params.getMaxValue() - params.getMinValue()) * ((i + 0.5) / params.getPopulationSize())));
                } else {
                    gene.randomize(params.getGenerationDistribution());
                }
                chromosome.add(gene);

                Entity e = EntityUtils.chromosome(chromosomeIdCounter.next(), chromosome, currentGenerationNumber);
                newChromosomes.add(e);
                engine.add(e);
            }
        } else {
            for (Chromosome chromosome : params.getInitialPopulation()) {
                Entity e = EntityUtils.chromosome(chromosomeIdCounter.next(), chromosome, currentGenerationNumber);
                newChromosomes.addAll(e);
                engine.add(e);
            }
        }

        currentGeneration = EntityUtils.generation(1000 + currentGenerationNumber, currentGenerationNumber, null, newChromosomes);
        engine.add(currentGeneration);
        generationMemory.addLast(currentGeneration);
        updateChromosomesPositionsIfDirty(newChromosomes);
        if (params.getFunction() != null) {
            updateChromosomesFunctionValue(params.getFunction(), newChromosomes);
        }
        if (params.getFitnessFunction() != null){
            params.getFitnessFunction().calculateFitness(engine, newChromosomes);
        }
        bestChromosomeOfGeneration = selectBestChromosomeOfGeneration(newChromosomes);
        dispatch(new GenerationChangedEvent(currentGenerationNumber, currentGeneration, bestChromosomeOfGeneration));
    }
}
