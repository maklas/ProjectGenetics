package ru.maklas.genetics.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.engine.genetics.CrossoverReproductionFunction;
import ru.maklas.genetics.engine.genetics.FunctionMinimalValueFitnessFunction;
import ru.maklas.genetics.engine.genetics.RandomBitChangeMutation;
import ru.maklas.genetics.mnw.MNW;
import ru.maklas.genetics.user_interface.MainMenuView;
import ru.maklas.genetics.utils.functions.*;
import ru.maklas.genetics.utils.gsm_lib.State;

import static ru.maklas.genetics.utils.StringUtils.df;
import static ru.maklas.genetics.utils.StringUtils.ff;

public class MainMenuState extends State {

    MainMenuView mm;
    GraphFunction function = new DampedSineWaveFunction(200f, 25, 0, 0.001f);

    @Override
    protected void onCreate() {
        A.skins.load();
        mm = new MainMenuView();

        mm.onStart(() -> {
            if (validate()) {
                Params params = new Params();
                params.setBitsPerGene(mm.getBitsPerGene());
                params.setFunction(function);
                params.setPopulationSize(mm.getPopulationSize());
                params.setMinMax(mm.getMin(), mm.getMax());
                params.setGenerationMemory(mm.getGenerationMemory());
                params.setReproductionFunction(new CrossoverReproductionFunction(mm.getCrossingPoints()));
                params.setMutationFunction(new RandomBitChangeMutation(mm.getBitMutationMin(), mm.getBitMutationMax()));
                params.setFitnessFunction(new FunctionMinimalValueFitnessFunction());
                pushState(new GeneticsGenerationState(params));
            }
        });
        mm.onSelectFunction(() -> pushState(new FunctionSelectionState(function)));

        setFunDesc(function);
        mm.setPopulationSize(25);
        mm.setBitsPerGeneLen(32);
        mm.setBitMutationMinMax(1, 1);
        mm.setCrossingPoints(1);
        mm.setGenerationMemory(10);
        mm.setMinMax(-100, 100);
        MNW.backgroundColor.set(0.95f, 0.95f, 0.95f, 1);
    }

    private boolean validate() {
        if (mm.getBitsPerGene() > 63) {
            gsm.print("Bits per gene [1..63]");
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
        if (mm.getCrossingPoints() < 1){
            gsm.print("Crossing points >= 1");
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


        return true;
    }

    private void setFunDesc(GraphFunction function) {
        String s;
        if (function instanceof LinearFunction){
            s = "Linear: " + df(((LinearFunction) function).k) + "x + " + df(((LinearFunction) function).b);
        } else
        if (function instanceof ParabolaFunction){
            s = "Quadratic: " + df(((ParabolaFunction) function).a) + "x^2 + " + df(((ParabolaFunction) function).b) + "x + " + df(((ParabolaFunction) function).c);
        } else
        if (function instanceof SineFunction){
            s = "Sine: " + df(((SineFunction) function).amp) + " * sin(x + " + df(((SineFunction) function).shift)+ ")";
        } else
        if (function instanceof DampedSineWaveFunction){
            s = "Damped Sine: " + df(((DampedSineWaveFunction) function).amp) + " * sin(x); Decay = " + df(((DampedSineWaveFunction) function).decay);
        } else
        if (function instanceof OnlyPositiveFunction){
            setFunDesc(((OnlyPositiveFunction) function).delegate);
            return;
        } else {
            s = function.getClass().getSimpleName();
        }
        mm.setFunctionDescription(s);
    }


    @Override
    protected void update(float dt) {
        mm.act(dt);
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
