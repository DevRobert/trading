package trading.challenges;

import trading.simulation.SimulationDriverParameters;

public interface Challenge {
    ParameterTupleSource buildParametersForDifferentRuns();
    SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters);
}
