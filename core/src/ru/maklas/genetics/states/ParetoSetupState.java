package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.genetics.engine.genetics.*;
import ru.maklas.genetics.utils.gsm_lib.State;

public class ParetoSetupState extends State {


    @Override
    protected void onCreate() {
        Params params = new Params();
        params.setFunction(null);
        params.setFitnessFunction(new ParetoFitnessFunction());
        params.setMutationFunction(new RandomBitChangeMutation(0, 2));
        params.setReproductionFunction(new CrossoverReproductionFunction(1));
        params.setGenerationMemory(10);
        params.setMinMax(-100, 100);
        params.setBitsPerGene(32);
        params.setGenerationDistribution(GenerationDistribution.RANDOM);
        params.setPopulationSize(30);
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
