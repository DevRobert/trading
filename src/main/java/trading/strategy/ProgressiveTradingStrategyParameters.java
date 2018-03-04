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

        if(buyTriggerRisingDaysInSequence < 0) {
            throw new StrategyInitializationException("The parameter 'buyTriggerRisingDaysInSequence' must not be negative.");
        }

        if(sellTriggerDecliningDays < 0) {
            throw new StrategyInitializationException("The parameter 'sellTriggerDecliningDays' must not be negative.");
        }

        if(sellTriggerMaxDays < 0) {
            throw new StrategyInitializationException("The parameter 'sellTriggerMaxDays' must not be negative.");
        }

        if(sellTriggerMaxDays == 0) {
            throw new StrategyInitializationException("The parameter 'sellTriggerMaxDays' must not be zero.");
        }

        if(restartTriggerDecliningDays < 0) {
            throw new StrategyInitializationException("The parameter 'restartTriggerDecliningDays' must not be negative.");
        }
    }

    public static ProgressiveTradingStrategyParameters parse(TradingStrategyParameters parameters) {
        String isinString;
        int buyTriggerRisingDaysInSequence;
        int sellTriggerDecliningDays;
        int sellTriggerNumMaxDays;
        int restartTriggerDecliningDays;

        try {

            isinString = parameters.getParameter("isin");
            buyTriggerRisingDaysInSequence = getIntegerParameter(parameters, "buyTriggerRisingDaysInSequence");
            sellTriggerDecliningDays = getIntegerParameter(parameters, "sellTriggerDecliningDays");
            sellTriggerNumMaxDays = getIntegerParameter(parameters, "sellTriggerMaxDays");
            restartTriggerDecliningDays = getIntegerParameter(parameters, "restartTriggerDecliningDays");
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

    private static int getIntegerParameter(TradingStrategyParameters parameters, String name) {
        String valueString = parameters.getParameter(name);
        int value;

        try {
            value = Integer.parseInt(valueString);
        }
        catch(Exception ex) {
            throw new StrategyInitializationException(String.format("The parameter '%s' is not a valid integer.", name));
        }

        return value;
    }

    public static ProgressiveTradingStrategyParameters getDefault() {
        final ISIN isin = ISIN.MunichRe;
        final int buyTriggerRisingDaysInSequence = 1;
        final int sellTriggerDecliningDays = 1;
        final int sellTriggerMaxDays = 3;
        final int restartTriggerDecliningDays = 0;

        return new ProgressiveTradingStrategyParameters(
                isin,
                buyTriggerRisingDaysInSequence,
                sellTriggerDecliningDays,
                sellTriggerMaxDays,
                restartTriggerDecliningDays
        );
    }
}
