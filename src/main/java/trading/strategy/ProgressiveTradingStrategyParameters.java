package trading.strategy;

import trading.ISIN;

public class ProgressiveTradingStrategyParameters {
    private final ISIN isin;
    private final int buyTriggerPositiveSeriesNumDays;
    private final int sellTriggerNumNegativeDays;
    private final int sellTriggerNumMaxDays;
    private final int restartTriggerNumNegativeDays;

    public ISIN getISIN() {
        return this.isin;
    }

    public int getBuyTriggerPositiveSeriesNumDays() {
        return buyTriggerPositiveSeriesNumDays;
    }

    public int getSellTriggerNumNegativeDays() {
        return sellTriggerNumNegativeDays;
    }

    public int getSellTriggerNumMaxDays() {
        return sellTriggerNumMaxDays;
    }

    public int getRestartTriggerNumNegativeDays() {
        return restartTriggerNumNegativeDays;
    }

    public ProgressiveTradingStrategyParameters(ISIN isin, int buyTriggerPositiveSeriesNumDays, int sellTriggerNumNegativeDays, int sellTriggerNumMaxDays, int restartTriggerNumNegativeDays) {
        this.isin = isin;
        this.buyTriggerPositiveSeriesNumDays = buyTriggerPositiveSeriesNumDays;
        this.sellTriggerNumNegativeDays = sellTriggerNumNegativeDays;
        this.sellTriggerNumMaxDays = sellTriggerNumMaxDays;
        this.restartTriggerNumNegativeDays = restartTriggerNumNegativeDays;
    }

    public static ProgressiveTradingStrategyParameters parse(TradingStrategyParameters parameters) {
        String isinString;
        int buyTriggerPositiveSeriesNumDays;
        int sellTriggerNumNegativeDays;
        int sellTriggerNumMaxDays;
        int restartTriggerNumNegativeDays;

        try {
            isinString = parameters.getParameter("isin");
            buyTriggerPositiveSeriesNumDays = getIntegerParameter(parameters, "buyTriggerPositiveSeriesNumDays", true, false);
            sellTriggerNumNegativeDays = getIntegerParameter(parameters, "sellTriggerNumNegativeDays", true, true);
            sellTriggerNumMaxDays = getIntegerParameter(parameters, "sellTriggerNumMaxDays", true, true);
            restartTriggerNumNegativeDays = getIntegerParameter(parameters, "restartTriggerNumNegativeDays", true, false);
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
