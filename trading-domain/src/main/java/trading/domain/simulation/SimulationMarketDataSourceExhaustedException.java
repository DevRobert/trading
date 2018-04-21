package trading.domain.simulation;

public class SimulationMarketDataSourceExhaustedException extends RuntimeException {
    public SimulationMarketDataSourceExhaustedException() {
        super("There are no further closing market prices available.");
    }
}
