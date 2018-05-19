package trading.api.strategy;

public class GetStrategyInformationResponse {
    private StrategyDto trading;
    private StrategyDto commission;

    public StrategyDto getTrading() {
        return this.trading;
    }

    public void setTrading(StrategyDto trading) {
        this.trading = trading;
    }

    public StrategyDto getCommission() {
        return this.commission;
    }

    public void setCommission(StrategyDto commissions) {
        this.commission = commissions;
    }
}
