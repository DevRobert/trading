package trading.api.market;

import java.time.LocalDate;
import java.util.List;

public class GetInstrumentsResponse {
    private LocalDate date;
    private List<InstrumentDto> instruments;

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<InstrumentDto> getInstruments() {
        return this.instruments;
    }

    public void setInstruments(List<InstrumentDto> instruments) {
        this.instruments = instruments;
    }
}
