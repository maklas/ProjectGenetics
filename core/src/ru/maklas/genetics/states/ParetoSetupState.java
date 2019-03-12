package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.genetics.engine.genetics.*;
import ru.maklas.genetics.functions.bi_functions.SerovNashBiFunction;
import ru.maklas.genetics.utils.gsm_lib.State;

public class ParetoSetupState extends State {


    @Override
    protected void onCreate() {
        Params params = new Params();
        params.setBiFunction1(new SerovNashBiFunction(70, 20, 0.2, 0.8));
        params.setBiFunction2(new SerovNashBiFunction(10, 70, 0.2, 0.8));
        params.setFitnessFunction(new ParetoFitnessFunction(2));
        params.setMutationFunction(new RandomBitChangeMutation(0, 1));
        params.setReproductionFunction(new CrossoverReproductionFunction(2));
        params.setGenerationMemory(10);
        params.setMinMax(0, 80);
        params.setBitsPerGene(32);
        params.setGenerationDistribution(GenerationDistribution.RANDOM);
        params.setPopulationSize(50);

        Chromosome c1 = EntityUtils.chromosome(params.getBitsPerGene(), params.getMinValue(), params.getMaxValue(), 10, 10);
        Chromosome c2 = EntityUtils.chromosome(params.getBitsPerGene(), params.getMinValue(), params.getMaxValue(), 40, 30);
        Chromosome c3 = EntityUtils.chromosome(params.getBitsPerGene(), params.getMinValue(), params.getMaxValue(), 30, 60);
        Chromosome c4 = EntityUtils.chromosome(params.getBitsPerGene(), params.getMinValue(), params.getMaxValue(), 60, 65);
        params.getInitialPopulation().addAll(c1, c2, c3, c4);
        pushState(new ParetoGeneticsState(params));
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            popState();
        }
    }

    @Override
    protected void render(Batch batch) {

    }

    @Override
    protected void dispose() {

    }
}
