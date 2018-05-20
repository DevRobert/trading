package trading.api.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import trading.domain.ISIN;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3001")
public class MarketController {
    @Autowired
    private MultiStockMarketDataStore multiStockMarketDataStore;

    @Autowired
    private InstrumentNameProvider instrumentNameProvider;

    @RequestMapping(value = "/api/market/instruments/", method = RequestMethod.GET)
    public GetInstrumentsResponse getInstruments() {
        MarketPriceSnapshot marketPriceSnapshot = this.multiStockMarketDataStore.getLastClosingPrices();

        GetInstrumentsResponse response = new GetInstrumentsResponse();
        response.setDate(marketPriceSnapshot.getDate());

        List<InstrumentDto> instrumentDtos = new ArrayList<>();

        for(ISIN isin: marketPriceSnapshot.getISINs()) {
            InstrumentDto instrumentDto = new InstrumentDto();

            instrumentDto.setIsin(isin.getText());
            instrumentDto.setMarketPrice(marketPriceSnapshot.getMarketPrice(isin).getValue());

            String name = this.instrumentNameProvider.getInstrumentName(isin);

            if(name == null) {
                name = "Unknown";
            }

            instrumentDto.setName(name);

            instrumentDtos.add(instrumentDto);
        }

        response.setInstruments(instrumentDtos);

        return  response;
    }
}
