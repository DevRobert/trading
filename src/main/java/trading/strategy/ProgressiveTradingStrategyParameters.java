package trading.strategy;

import trading.ISIN;

public class ProgressiveTradingStrategyParameters {
    private final ISIN isin;
    private final int buyTriggerRisingDaysInSequence;
    private final int sellTriggerDecliningDaysInSequence;
    private final int sellTriggerMaxDays;
    private final int restartTriggerDecliningDaysInSequence;

    public ISIN getISIN() {
        return this.isin;
    }

    public int getBuyTriggerRisingDaysInSequence() {
        return buyTriggerRisingDaysInSequence;
    }

    public int getSellTriggerDecliningDaysInSequence() {
        return sellTriggerDecliningDaysInSequence;
    }

    public int getSellTriggerMaxDays() {
        return sellTriggerMaxDays;
    }

    public int getRestartTriggerDecliningDaysInSequence() {
        return restartTriggerDecliningDaysInSequence;
    }

    public ProgressiveTradingStrategyParameters(ISIN isin, int buyTriggerRisingDaysInSequence, int sellTriggerDecliningDaysInSequence, int sellTriggerMaxDays, int restartTriggerDecliningDaysInSequence) {
        this.isin = isin;
        this.buyTriggerRisingDaysInSequence = buyTriggerRisingDaysInSequence;
        this.sellTriggerDecliningDaysInSequence = sellTriggerDecliningDaysInSequence;
        this.sellTriggerMaxDays = sellTriggerMaxDays;
        this.restartTriggerDecliningDaysInSequence = restartTriggerDecliningDaysInSequence;
    }

    public static ProgressiveTradingStrategyParameters parse(TradingStrategyParameters parameters) {
        String isinString;
        int buyTriggerPositiveSeriesNumDays;
        int sellTriggerNumNegativeDays;
        int sellTriggerNumMaxDays;
        int restartTriggerNumNegativeDays;

        try {
            isinString = parameters.getParameter("isin");
            buyTriggerPositiveSeriesNumDays = getIntegerParameter(parameters, "buyTriggerRisingDaysInSequence", true, false);
            sellTriggerNumNegativeDays = getIntegerParameter(parameters, "sellTriggerDecliningDaysInSequence", true, true);
            sellTriggerNumMaxDays = getIntegerParameter(parameters, "sellTriggerMaxDays", true, true);
            restartTriggerNumNegativeDays = getIntegerParameter(parameters, "restartTriggerDecliningDaysInSequence", true, false);
        }
        catch(MissingParameterException ex) {
            throw new StrategyInitializationException(ex.getMessage());
        }

        ISIN isin = new ISIN(isinString);

        return new ProgressiveTradingStrategyParameters(
                isin,
                buyTriggerPositiveSeriesNumDays,
                sellTriggerNumNegativeDays,
                sellTriggerNumMaxDays,
                restartTriggerNumNegativeDays);
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
