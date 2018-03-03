package trading.strategy;

import java.util.HashMap;
import java.util.Map;

public class TradingStrategyParametersBuilder {
    private final Map<String, String> parameters = new HashMap<>();

    public void setParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    public TradingStrategyParameters build() {
        return new TradingStrategyParameters(this.parameters);
    }
}
