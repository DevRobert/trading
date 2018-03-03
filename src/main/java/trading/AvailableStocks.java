package trading;

import java.util.Set;

public class AvailableStocks {
    private final Set<ISIN> isins;

    public AvailableStocks(Set<ISIN> isins) {
        this.isins = isins;
    }

    public Set<ISIN> getISINs() {
        return isins;
    }
}
