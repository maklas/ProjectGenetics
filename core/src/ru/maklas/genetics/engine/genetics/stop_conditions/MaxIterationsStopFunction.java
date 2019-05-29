package ru.maklas.genetics.engine.genetics.stop_conditions;

import ru.maklas.genetics.engine.genetics.ChromosomeSystem;
import ru.maklas.genetics.engine.genetics.StopConditionFunction;
import ru.maklas.mengine.Engine;

public class MaxIterationsStopFunction implements StopConditionFunction {

    private int maxGen;

    public MaxIterationsStopFunction(int maxGen) {
        this.maxGen = maxGen;
    }

    public int getMaxGen() {
        return maxGen;
    }

    public void setMaxGen(int maxGen) {
        this.maxGen = maxGen;
    }

    @Override
    public boolean shouldStop(Engine engine) {
        int currentGenerationNumber = engine.getSystemManager().getExtendableSystem(ChromosomeSystem.class).currentGenerationNumber;
        return currentGenerationNumber >= maxGen;
    }
}
