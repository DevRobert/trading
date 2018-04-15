package trading.strategy;

import trading.ISIN;

public interface TriggerFactory {
    Trigger createTrigger(ISIN isin);
}
