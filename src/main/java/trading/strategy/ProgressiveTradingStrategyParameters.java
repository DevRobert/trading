package trading.strategy;

import trading.ISIN;

public class ProgressiveTradingStrategyParameters {
    private final ISIN isin;
    private final int buyTriggerRisingDaysInSequence;
    private final int sellTriggerDecliningDays;
    private final int sellTriggerMaxDays;
    private final int restartTriggerDecliningDays;

    public ISIN getISIN() {
        return this.isin;
    }

    public int getBuyTriggerRisingDaysInSequence() {
        return buyTriggerRisingDaysInSequence;
    }

    public int getSellTriggerDecliningDays() {
        return sellTriggerDecliningDays;
    }

    public int getSellTriggerMaxDays() {
        return sellTriggerMaxDays;
    }

    public int getRestartTriggerDecliningDays() {
        return restartTriggerDecliningDays;
    }

    public ProgressiveTradingStrategyParameters(ISIN isin, int buyTriggerRisingDaysInSequence, int sellTriggerDecliningDays, int sellTriggerMaxDays, int restartTriggerDecliningDays) {
        this.isin = isin;
        this.buyTriggerRisingDaysInSequence = buyTriggerRisingDaysInSequence;
        this.sellTriggerDecliningDays = sellTriggerDecliningDays;
        this.sellTriggerMaxDays = sellTriggerMaxDays;
        this.restartTriggerDecliningDays = restartTriggerDecliningDays;
    }

    public static ProgressiveTradingStrategyParameters parse(TradingStrategyParameters parameters) {
        String isinString;
        int buyTriggerRisingDaysInSequence;
        int sellTriggerDecliningDays;
        int sellTriggerNumMaxDays;
        int restartTriggerDecliningDays;

        try {
            isinString = parameters.getParameter("isin");
            buyTriggerRisingDaysInSequence = getIntegerParameter(parameters, "buyTriggerRisingDaysInSequence", true, false);
            sellTriggerDecliningDays = getIntegerParameter(parameters, "sellTriggerDecliningDays", true, true);
            sellTriggerNumMaxDays = getIntegerParameter(parameters, "sellTriggerMaxDays", true, true);
            restartTriggerDecliningDays = getIntegerParameter(parameters, "restartTriggerDecliningDays", true, false);
        }
        catch(MissingParameterException ex) {
            throw new StrategyInitializationException(ex.getMessage());
        }

        ISIN isin = new ISIN(isinString);

        return new ProgressiveTradingStrategyParameters(
                isin,
                buyTriggerRisingDaysInSequence,
                sellTriggerDecliningDays,
                sellTriggerNumMaxDays,
                restartTriggerDecliningDays);
    }

    // TODO introduce validation mode
    // TODO extract "helper" class, also available for other trading strategies

    private static int getIntegerParameter(TradingStrategyParameters parameters, String name, boolean nonNegative, boolean nonZero) {
        String valueString = parameters.getParameter(name);
        int value;

        try {
            value = Integer.parseInt(valueString);
        }
        catch(Exception ex) {
            throw new StrategyInitializationException(String.format("The parameter '%s' is not a valid integer.", name));
        }

        if(nonNegative && value < 0) {
            throw new StrategyInitializationException(String.format("The parameter '%s' must not be negative.", name));
        }

        if(nonZero && value == 0) {
            throw new StrategyInitializationException(String.format("The parameter '%s' must not be zero.", name));
        }

        return value;
    }
}
