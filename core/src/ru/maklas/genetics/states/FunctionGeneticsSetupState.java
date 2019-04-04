package ru.maklas.genetics.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.genetics.engine.genetics.CrossoverReproductionFunction;
import ru.maklas.genetics.engine.genetics.FunctionMinimalValueFitnessFunction;
import ru.maklas.genetics.engine.genetics.RandomBitChangeMutation;
import ru.maklas.genetics.functions.*;
import ru.maklas.genetics.user_interface.FunctionGeneticsView;
import ru.maklas.genetics.utils.StringUtils;
import ru.maklas.genetics.utils.gsm_lib.State;

public class FunctionGeneticsSetupState extends State {

    FunctionGeneticsView mm;
    GraphFunction function = new SineWaveFunction(50, 75, 0, 0.001);

    @Override
    protected void onCreate() {
        mm = new FunctionGeneticsView();

        mm.onStart(() -> {
            if (validate()) {
                Params params = new Params();
                params.setBitsPerGene(mm.getBitsPerGene());
                params.setFunction(function);
                params.setPopulationSize(mm.getPopulationSize());
                params.setMinMax(mm.getMin(), mm.getMax());
                params.setGenerationMemory(mm.getGenerationMemory());
                params.setReproductionFunction(new CrossoverReproductionFunction(mm.getCrossingPoints()));
                params.setMutationFunction(new RandomBitChangeMutation(mm.getBitMutationMin(), mm.getBitMutationMax(), mm.getMutationChance()));
                params.setMutationChance(mm.getMutationChance());
                params.setFitnessFunction(new FunctionMinimalValueFitnessFunction(mm.getQ()));
                params.setGenerationDistribution(mm.getGenerationDistribution());
                pushState(new FunctionGeneticsState(params));
            }
        });
        mm.onSelectFunction(() -> pushState(new FunctionSelectionState(function)));

        setFunDesc(function);
        mm.setPopulationSize(25);
        mm.setBitsPerGeneLen(32);
        mm.setBitMutationMinMax(1, 1);
        mm.setCrossingPoints(1);
        mm.setGenerationMemory(10);
        mm.setMinMax(-150, 150);
        mm.setQ(2);
        mm.setMutationChance(90);
    }

    private boolean validate() {
        if (mm.getBitsPerGene() > 62) {
            gsm.print("Bits per gene [1..62]");
            return false;
        }
        if (mm.getPopulationSize() < 2){
            gsm.print("Population size >= 2");
            return false;
        }
        if (mm.getMin() >= mm.getMax()){
            gsm.print("min < max");
            return false;
        }
        if (mm.getGenerationMemory() < 1){
            gsm.print("Generation memory >= 1");
            return false;
        }
        if (mm.getCrossingPoints() < 0){
            gsm.print("Crossing points >= 0");
            return false;
        }
        if (mm.getCrossingPoints() >= mm.getBitsPerGene()){
            gsm.print("Crossing points < " + mm.getBitsPerGene() + " (bits per gene)");
            return false;
        }
        if (mm.getBitMutationMin() < 0){
            gsm.print("Bit mutations >= 0");
            return false;
        }
        if (mm.getBitMutationMin() > mm.getBitMutationMax()){
            gsm.print("Bit mutations min < Bit mutations max");
            return false;
        }
        if (mm.getBitMutationMax() >= mm.getBitsPerGene()){
            gsm.print("Bit mutations max < Bits per gene");
            return false;
        }

        if (mm.getQ() <= 0){
            gsm.print("Q > 0");
            return false;
        }


        return true;
    }

    private void setFunDesc(GraphFunction function) {
        String s;
        if (function instanceof ParabolaFunction){
            s = "Quadratic";
        } else
        if (function instanceof SineWaveFunction){
            s = "Sine wave";
        } else
        if (function instanceof TriangleWaveFunction){
            s = "Triangle wave";
        } else
        if (function instanceof CustomFunction){
            s = "Custom";
        } else
        if (function instanceof SinusoidalAMFunction){
            s = "AM function";
        } else
        if (function instanceof DeBroglieFunction){
            s = "De Broglie matter wave";
        } else {
            s = function.getClass().getSimpleName();
        }
        mm.setFunctionDescription(s);
    }


    @Override
    protected void update(float dt) {
        mm.act(dt);
        mm.setPrecision(calculatePrecision());
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            popState();
        }
    }

    private String calculatePrecision() {
        int bitsPerGene = mm.getBitsPerGene();
        double max = mm.getMax();
        double min = mm.getMin();
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
        return mm;
    }

    @Override
    protected void onResume(State from) {
        if (from instanceof FunctionSelectionState){
            GraphFunction selectedFunction = ((FunctionSelectionState) from).selectedFunction;
            function = selectedFunction;
            setFunDesc(function);
        }
    }

    @Override
    public void resize(int width, int height) {
        mm.resize(width, height);
    }

    @Override
    protected void render(Batch batch) {
        mm.draw();
    }

    @Override
    protected void dispose() {
        mm.dispose();
    }
}
