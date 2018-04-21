package trading.domain.challenges;

import trading.domain.simulation.SimulationDriverParameters;

public interface Challenge {
    ParameterTupleSource getParametersSource();
    SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters);
    String[] getParameterNames();
}
