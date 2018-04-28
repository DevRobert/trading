package trading.domain.market;

import trading.domain.ISIN;

public interface InstrumentNameProvider {
    String getInstrumentName(ISIN isin);
}
