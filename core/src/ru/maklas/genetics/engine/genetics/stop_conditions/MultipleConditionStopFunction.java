package ru.maklas.genetics.engine.genetics.stop_conditions;

import com.badlogic.gdx.utils.Array;
import ru.maklas.genetics.engine.genetics.StopConditionFunction;
import ru.maklas.mengine.Engine;

public class MultipleConditionStopFunction implements StopConditionFunction {

    private Array<StopConditionFunction> functions = new Array<>();


    public MultipleConditionStopFunction() {

    }

    public MultipleConditionStopFunction add(StopConditionFunction fun){
        functions.add(fun);
        return this;
    }

    @Override
    public boolean shouldStop(Engine engine) {
        for (StopConditionFunction function : functions) {
            if (function.shouldStop(engine)) {
                return true;
            }
        }
        return false;
    }
}
