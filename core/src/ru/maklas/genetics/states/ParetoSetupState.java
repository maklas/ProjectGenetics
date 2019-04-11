package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.genetics.engine.genetics.*;
import ru.maklas.genetics.functions.bi_functions.SerovNashBiFunction;
import ru.maklas.genetics.user_interface.XYSettingsView;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.genetics.utils.gsm_lib.State;

public class ParetoSetupState extends State {

    XYSettingsView view;

    @Override
    protected void onCreate() {
        view = new XYSettingsView();

        view.onStart(() -> {
            if (validate()) {
                Params params = new Params();

                if (view.getRandomizeFunctions()){
                    params.setBiFunction1(SerovNashBiFunction.rand(view.getMin(), view.getMax()));
                    params.setBiFunction2(SerovNashBiFunction.rand(view.getMin(), view.getMax()));
                } else {
                    params.setBiFunction1(new SerovNashBiFunction(100, 250, 0.2, 0.8));
                    params.setBiFunction2(new SerovNashBiFunction(250, 100, 0.8, 0.2));
                }
                params.setFitnessFunction(new ParetoFitnessFunction(view.getQ()));
                params.setBitsPerGene(view.getBitsPerGene());
                params.setPopulationSize(view.getPopulationSize());
                params.setMinMax(view.getMin(), view.getMax());
                params.setGenerationMemory(view.getGenerationMemory());
                params.setReproductionFunction(new CrossoverReproductionFunction(view.getCrossingPoints()));
                params.setMutationFunction(new RandomBitChangeMutation(view.getBitMutationMin(), view.getBitMutationMax(), view.getMutationChance()));
                params.setMutationChance(view.getMutationChance());
                params.setGenerationDistribution(GenerationDistribution.RANDOM);

                pushState(new BiFunGeneticsState(params));
            }
        });
        
        view.setPopulationSize(500);
        view.setBitsPerGeneLen(20);
        view.setBitMutationMinMax(1, 1);
        view.setCrossingPoints(1);
        view.setGenerationMemory(1);
        view.setMinMax(0, 300);
        view.setQ(150);
        view.setMutationChance(9);
        view.setRandomizeFunctions(false);

    }

    private boolean validate() {
        if (view.getBitsPerGene() > 62) {
            gsm.print("Bits per gene [1..62]");
            return false;
        }
        if (view.getPopulationSize() < 2){
            gsm.print("Population size >= 2");
            return false;
        }
        if (view.getMin() >= view.getMax()){
            gsm.print("min < max");
            return false;
        }
        if (view.getGenerationMemory() < 1){
            gsm.print("Generation memory >= 1");
            return false;
        }
        if (view.getCrossingPoints() < 0){
            gsm.print("Crossing points >= 0");
            return false;
        }
        if (view.getCrossingPoints() >= view.getBitsPerGene()){
            gsm.print("Crossing points < " + view.getBitsPerGene() + " (bits per gene)");
            return false;
        }
        if (view.getBitMutationMin() < 0){
            gsm.print("Bit mutations >= 0");
            return false;
        }
        if (view.getBitMutationMin() > view.getBitMutationMax()){
            gsm.print("Bit mutations min < Bit mutations max");
            return false;
        }
        if (view.getBitMutationMax() >= view.getBitsPerGene()){
            gsm.print("Bit mutations max < Bits per gene");
            return false;
        }

        if (view.getQ() <= 0){
            gsm.print("Q > 0");
            return false;
        }


        return true;
    }

    @Override
    protected void update(float dt) {
        view.act(dt);
        view.setPrecision(calculatePrecision());
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            popState();
        }

        double q = view.getQ();
        int populationSize = view.getPopulationSize();
        if (populationSize > 0 && q > 0){
            double secondBest = 1 / (Math.pow(1d + (1 / (populationSize - 1d)), q));
            double last = 1 / (Math.pow(2, q));
            view.setSecondBest(secondBest);
            view.setLast(last);
        }
    }


    private String calculatePrecision() {
        int bitsPerGene = view.getBitsPerGene();
        double max = view.getMax();
        double min = view.getMin();
        double width = max - min;
        if (width <= 0d || bitsPerGene < 1 || bitsPerGene >= 63){
            return "err";
        }

        double totalPlaces = Math.pow(2, bitsPerGene);
        double precision = width / totalPlaces;
        int logFloor = (int) Math.floor(Math.log10(precision));
        if (logFloor >= 0) return StringUtils.df(precision, 2);
        return StringUtils.df(precision, -(logFloor - 1));
    }

    @Override
    protected InputProcessor getInput() {
        return view;
    }


    @Override
    public void resize(int width, int height) {
        view.resize(width, height);
    }

    @Override
    protected void render(Batch batch) {
        view.draw();
    }

    @Override
    protected void dispose() {
        view.dispose();
    }
}
