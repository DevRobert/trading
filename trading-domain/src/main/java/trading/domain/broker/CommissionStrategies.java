package trading.domain.broker;

import trading.domain.Amount;

public abstract class CommissionStrategies {
    private static CommissionStrategy zeroCommissionStrategy;
    private static DynamicCommissionStrategyParameters consorsXetraCommissionStrategyParameters;
    private static DynamicCommissionStrategyParameters degiroXetraCommissionStrategyParameters;
    private static CommissionStrategy consorsXetraCommissionStrategy;
    private static CommissionStrategy degiroXetraCommissionStrategy;

    public static CommissionStrategy getZeroCommissionStrategy() {
        return zeroCommissionStrategy;
    }

    public static DynamicCommissionStrategyParameters getConsorsXetraCommissionStrategyParameters() {
        return consorsXetraCommissionStrategyParameters;
    }

    public static CommissionStrategy getConsorsXetraCommissionStrategy() {
        return consorsXetraCommissionStrategy;
    }

    public static DynamicCommissionStrategyParameters getDegiroXetraCommissionStrategyParameters() {
        return degiroXetraCommissionStrategyParameters;
    }

    public static CommissionStrategy getDegiroXetraCommissionStrategy() {
        return degiroXetraCommissionStrategy;
    }

    static {
        buildZeroCommissionStrategy();
        buildConsorsXetraCommissionStratgey();
        buildDegiroXetraCommissionStrategy();
    }

    private static void buildZeroCommissionStrategy() {
        zeroCommissionStrategy = new ZeroCommissionStrategy();
    }

    private static void buildConsorsXetraCommissionStratgey() {
        consorsXetraCommissionStrategyParameters = new DynamicCommissionStrategyParametersBuilder()
                .setFixedAmount(new Amount(4.95 + 1.50))
                .setVariableAmountRate(0.0025)
                .setMinimumVariableAmount(new Amount(9.95))
                .setMaximumVariableAmount(new Amount(69.00))
                .build();

        consorsXetraCommissionStrategy = new DynamicCommissionStrategy(consorsXetraCommissionStrategyParameters);
    }

    private static void buildDegiroXetraCommissionStrategy() {
        degiroXetraCommissionStrategyParameters = new DynamicCommissionStrategyParametersBuilder()
                .setFixedAmount(new Amount(2.0))
                .setVariableAmountRate(0.00008)
                .setMinimumVariableAmount(new Amount(0.0))
                .setMaximumVariableAmount(new Amount(28.0))
                .build();

        degiroXetraCommissionStrategy = new DynamicCommissionStrategy(degiroXetraCommissionStrategyParameters);
    }
}
