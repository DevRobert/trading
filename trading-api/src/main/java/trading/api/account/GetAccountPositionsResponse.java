package trading.api.account;

import java.util.List;

public class GetAccountPositionsResponse {
    private List<AccountPositionDto> positions;

    public List<AccountPositionDto> getPositions() {
        return positions;
    }

    public void setPositions(List<AccountPositionDto> positions) {
        this.positions = positions;
    }
}
