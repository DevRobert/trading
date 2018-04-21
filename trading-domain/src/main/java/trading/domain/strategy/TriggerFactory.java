package trading.domain.strategy;

import trading.domain.ISIN;

public interface TriggerFactory {
    Trigger createTrigger(ISIN isin);
}
