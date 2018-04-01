package trading.challenges;

import trading.simulation.SimulationDriverParameters;

public interface Challenge {
    ParameterTupleSource getParametersSource();
    SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters);
    String[] getParameterNames();
}
