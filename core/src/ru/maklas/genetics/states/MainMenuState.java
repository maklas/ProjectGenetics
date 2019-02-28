package ru.maklas.genetics.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.genetics.assets.A;
import ru.maklas.genetics.user_interface.MainMenuView;
import ru.maklas.genetics.utils.functions.*;
import ru.maklas.genetics.utils.gsm_lib.State;

import static ru.maklas.genetics.utils.StringUtils.ff;

public class MainMenuState extends State {

    MainMenuView mm;
    GraphFunction function = new ParabolaFunction(0.1f, 0, 0);

    @Override
    protected void onCreate() {
        A.skins.load();
        mm = new MainMenuView();

        mm.onStart(() -> {
            Params params = new Params();
            params.setBitsPerGene(32);
            params.setFunction(function);
            params.setChromosomesPerGeneration(100);
            params.setMinMax(-300, 300);
            params.setGenerationMemory(100);
            pushState(new GeneticsGenerationState(params));
        });
        mm.onSelectFunction(() -> pushState(new FunctionSelectionState(function)));

        setFunDesc(function);

    }

    private void setFunDesc(GraphFunction function) {
        String s;
        if (function instanceof LinearFunction){
            s = "Linear: " + ff(((LinearFunction) function).k) + "x + " + ff(((LinearFunction) function).b);
        } else
        if (function instanceof ParabolaFunction){
            s = "Quadratic: " + ff(((ParabolaFunction) function).a) + "x^2 + " + ff(((ParabolaFunction) function).b) + "x + " + ff(((ParabolaFunction) function).c);
        } else
        if (function instanceof SineFunction){
            s = "Sine: " + ff(((SineFunction) function).amp) + " * sin(x + " + ff(((SineFunction) function).shift)+ ")";
        } else
        if (function instanceof DampedSineWaveFunction){
            s = "Damped Sine: " + ff(((DampedSineWaveFunction) function).amp) + " * sin(x); Decay = " + ff(((DampedSineWaveFunction) function).decay);
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
