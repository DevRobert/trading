package trading.domain.strategy;

import trading.domain.Amount;
import trading.domain.Quantity;
import trading.domain.broker.CommissionStrategy;

public class AffordableQuantityCalculator {
    public Quantity calculateAffordableQuantity(Amount availableMoney, Amount lastMarketPrice, CommissionStrategy commissionStrategy) {
        int maxQuantity = (int) (availableMoney.getValue() / lastMarketPrice.getValue());

        for(int quantity = maxQuantity; quantity > 0; quantity--) {
            Amount totalPrice = lastMarketPrice.multiply(new Quantity(quantity));
            Amount commission = commissionStrategy.calculateCommission(totalPrice);
            Amount sum = totalPrice.add(commission);

            if(availableMoney.getValue() >= sum.getValue()) {
                return new Quantity(quantity);
            }
        }

        return Quantity.Zero;
    }
}
