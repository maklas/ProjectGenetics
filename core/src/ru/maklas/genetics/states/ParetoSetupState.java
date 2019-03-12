package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.genetics.engine.genetics.*;
import ru.maklas.genetics.functions.bi_functions.SerovNashBiFunction;
import ru.maklas.genetics.user_interface.ParetoView;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.genetics.utils.gsm_lib.State;

public class ParetoSetupState extends State {

    ParetoView view;

    @Override
    protected void onCreate() {
        view = new ParetoView();

        view.onStart(() -> {
            if (validate()) {
                Params params = new Params();

                params.setBiFunction1(new SerovNashBiFunction(70, 20, 0.2, 0.8));
                params.setBiFunction2(new SerovNashBiFunction(10, 70, 0.2, 0.8));
                params.setFitnessFunction(new ParetoFitnessFunction(view.getQ()));
                params.setBitsPerGene(view.getBitsPerGene());
                params.setPopulationSize(view.getPopulationSize());
                params.setMinMax(view.getMin(), view.getMax());
                params.setGenerationMemory(view.getGenerationMemory());
                params.setReproductionFunction(new CrossoverReproductionFunction(view.getCrossingPoints()));
                params.setMutationFunction(new RandomBitChangeMutation(view.getBitMutationMin(), view.getBitMutationMax()));



                Chromosome c1 = EntityUtils.chromosome(params.getBitsPerGene(), params.getMinValue(), params.getMaxValue(), 10, 10);
                Chromosome c2 = EntityUtils.chromosome(params.getBitsPerGene(), params.getMinValue(), params.getMaxValue(), 40, 30);
                Chromosome c3 = EntityUtils.chromosome(params.getBitsPerGene(), params.getMinValue(), params.getMaxValue(), 30, 60);
                Chromosome c4 = EntityUtils.chromosome(params.getBitsPerGene(), params.getMinValue(), params.getMaxValue(), 60, 65);
                params.getInitialPopulation().addAll(c1, c2, c3, c4);

                pushState(new ParetoGeneticsState(params));
            }
        });
        
        view.setPopulationSize(25);
        view.setBitsPerGeneLen(32);
        view.setBitMutationMinMax(1, 1);
        view.setCrossingPoints(1);
        view.setGenerationMemory(10);
        view.setMinMax(0, 80);
        view.setQ(2);
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
        if (view.getCrossingPoints() < 1){
            gsm.print("Crossing points >= 1");
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
