package trading.api.account;

import java.util.List;

public class GetAccountPositionsResponse {
    private List<AccountPositionDto> positions;
    private AccountSummaryDto summary;

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
}
