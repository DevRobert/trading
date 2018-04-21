package trading.domain.broker;

import trading.domain.Amount;

public class CommissionStrategies {
    private static CommissionStrategy zeroCommissionStrategy;
    private static CommissionStrategy consorsXetraCommissionStrategy;
    private static CommissionStrategy degiroXetraCommissionStrategy;

    public static CommissionStrategy getZeroCommissionStrategy() {
        return zeroCommissionStrategy;
    }

    public static CommissionStrategy getConsorsXetraCommissionStrategy() {
        return consorsXetraCommissionStrategy;
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
        DynamicCommissionStrategyParametersBuilder parametersBuilder = new DynamicCommissionStrategyParametersBuilder();

        parametersBuilder.setFixedAmount(new Amount(4.95 + 1.50));
        parametersBuilder.setVariableAmountRate(0.0025);
        parametersBuilder.setMinimumVariableAmount(new Amount(9.95));
        parametersBuilder.setMaximumVariableAmount(new Amount(69.00));

        consorsXetraCommissionStrategy = new DynamicCommissionStrategy(parametersBuilder.build());
    }

    private static void buildDegiroXetraCommissionStrategy() {
        DynamicCommissionStrategyParametersBuilder parametersBuilder = new DynamicCommissionStrategyParametersBuilder();

        parametersBuilder.setFixedAmount(new Amount(2.0));
        parametersBuilder.setVariableAmountRate(0.00008);
        parametersBuilder.setMinimumVariableAmount(new Amount(0.0));
        parametersBuilder.setMaximumVariableAmount(new Amount(28.0));

        degiroXetraCommissionStrategy = new DynamicCommissionStrategy(parametersBuilder.build());
    }
}
