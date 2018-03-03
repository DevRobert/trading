package trading.strategy;

import java.util.Map;

public class TradingStrategyParameters {
    private final Map<String, String> parameters;

    public TradingStrategyParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name) {
        String value = this.parameters.get(name);

        if(value == null) {
            throw new MissingParameterException(name);
        }

        return value;
    }
}
