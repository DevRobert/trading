package trading.challenges;

import trading.simulation.SimulationDriverParameters;

import java.util.List;

public interface Challenge {
    List<Object[]> buildParametersForDifferentRuns();
    SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters);
}
