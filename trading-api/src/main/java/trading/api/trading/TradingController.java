package trading.api.trading;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trading.application.AccountService;
import trading.application.TradeList;
import trading.application.TradingConfiguration;
import trading.application.TradingService;
import trading.domain.Amount;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.broker.CommissionStrategy;
import trading.domain.broker.OrderRequest;
import trading.domain.broker.OrderType;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3001")
public class TradingController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private TradingService tradingService;

    @Autowired
    private InstrumentNameProvider instrumentNameProvider;

    @Autowired
    private MultiStockMarketDataStore multiStockMarketDataStore;

    @RequestMapping("/api/trades/")
    public CalculateTradesResponse calculateTrades() {
        Account account = this.accountService.getAccount(new AccountId(1));
        TradeList tradeList = this.tradingService.calculateTrades(account);
        MarketPriceSnapshot lastClosingPrices = this.multiStockMarketDataStore.getLastClosingPrices();
        CommissionStrategy commissionStrategy = TradingConfiguration.getCommissionStrategy();

        CalculateTradesResponse response = new CalculateTradesResponse();

        response.setMarketPricesDate(tradeList.getDate());

        List<TradeDto> tradeDtos = new ArrayList<>();

        for(OrderRequest orderRequest: tradeList.getTrades()) {
            TradeDto tradeDto = new TradeDto();

            tradeDto.setType(getOrderTypeString(orderRequest.getOrderType()));
            tradeDto.setIsin(orderRequest.getIsin().getText());

            String company = this.instrumentNameProvider.getInstrumentName(orderRequest.getIsin());

            if(company == null) {
                company = "Unknown";
            }

            tradeDto.setName(company);

            tradeDto.setQuantity(orderRequest.getQuantity().getValue());
            tradeDto.setMarketPrice(lastClosingPrices.getMarketPrice(orderRequest.getIsin()).getValue());
            tradeDto.setTotalPrice(tradeDto.getMarketPrice() * tradeDto.getQuantity());
            tradeDto.setCommission(commissionStrategy.calculateCommission(new Amount(tradeDto.getTotalPrice())).getValue());

            tradeDtos.add(tradeDto);
        }

        response.setTrades(tradeDtos);

        return response;
    }

    private static String getOrderTypeString(OrderType orderType) {
        switch(orderType) {
            case BuyMarket:
                return "Buy";

            case SellMarket:
                return "Sell";

            default:
                return "Unknown";
        }
    }
}
