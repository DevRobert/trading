package trading.api.strategy;

import java.util.List;

public class StrategyDto {
    private String name;
    private List<StrategyParameterDto> parameters;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StrategyParameterDto> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<StrategyParameterDto> parameters) {
        this.parameters = parameters;
    }
}
