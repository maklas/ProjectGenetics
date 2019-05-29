package ru.maklas.genetics.engine.genetics.stop_conditions;

import ru.maklas.genetics.engine.EngineUtils;
import ru.maklas.genetics.engine.genetics.StopConditionFunction;
import ru.maklas.mengine.Engine;

public class ElitePointPercentageStopFunction implements StopConditionFunction {

    private double minElitePointsPercent;
    private double minFitness;

    /**
     * @param minElitePointsPercent 0..1
     * @param minFitness 0..1
     */
    public ElitePointPercentageStopFunction(double minElitePointsPercent, double minFitness) {
        this.minElitePointsPercent = minElitePointsPercent;
        this.minFitness = minFitness;
    }

    @Override
    public boolean shouldStop(Engine engine) {
        return EngineUtils.getElitePointsPercent(engine, minFitness) >= minElitePointsPercent;
    }
}
