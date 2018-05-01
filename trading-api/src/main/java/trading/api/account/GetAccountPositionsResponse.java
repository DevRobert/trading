package trading.api.account;

import java.time.LocalDate;
import java.util.List;

public class GetAccountPositionsResponse {
    private List<AccountPositionDto> positions;
    private AccountSummaryDto summary;
    private LocalDate marketPricesDate;

    public List<AccountPositionDto> getPositions() {
        return positions;
    }

    public void setPositions(List<AccountPositionDto> positions) {
        this.positions = positions;
    }

    public AccountSummaryDto getSummary() {
        return summary;
    }

    public void setSummary(AccountSummaryDto summary) {
        this.summary = summary;
    }

    public LocalDate getMarketPricesDate() {
        return this.marketPricesDate;
    }

    public void setMarketPricesDate(LocalDate marketPricesDate) {
        this.marketPricesDate = marketPricesDate;
    }
}
