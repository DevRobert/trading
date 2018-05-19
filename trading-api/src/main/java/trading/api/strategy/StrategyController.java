package trading.api.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import trading.application.TradingConfigurationService;
import trading.domain.broker.DynamicCommissionStrategyParameters;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategyParameters;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3001")
public class StrategyController {
    @Autowired
    private TradingConfigurationService tradingConfigurationService;

    @RequestMapping(value = "/api/strategy", method = RequestMethod.GET)
    public GetStrategyInformationResponse getStrategyInformation() {
        GetStrategyInformationResponse response = new GetStrategyInformationResponse();

        this.buildTradingStrategyInformation(response);
        this.buildCommissionStrategyInformation(response);

        return response;
    }

    private void buildTradingStrategyInformation(GetStrategyInformationResponse response) {
        CompoundLocalMaximumTradingStrategyParameters tradingStrategyParameters = this.tradingConfigurationService.getTradingStrategyParameters();

        StrategyDto tradingStrategyDto = new StrategyDto();
        tradingStrategyDto.setName("CompoundLocalMaximumTradingStrategy");

        List<StrategyParameterDto> tradingStrategyParameterDtos = new ArrayList<>();

        tradingStrategyParameterDtos.add(new StrategyParameterDto("maximum_look_behind", ((Integer)tradingStrategyParameters.getBuyTriggerLocalMaximumLookBehindPeriod().getValue()).toString()));
        tradingStrategyParameterDtos.add(new StrategyParameterDto("maximum_min_decline", ((Double)tradingStrategyParameters.getBuyTriggerMinDeclineFromLocalMaximumPercentage()).toString()));
        tradingStrategyParameterDtos.add(new StrategyParameterDto("trailing_stop_loss_min_decline", ((Double)tradingStrategyParameters.getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage()).toString()));
        tradingStrategyParameterDtos.add(new StrategyParameterDto("trailing_stop_loss_activation_min_raise", ((Double)tradingStrategyParameters.getActivateTrailingStopLossMinRaiseSinceBuyingPercentage()).toString()));
        tradingStrategyParameterDtos.add(new StrategyParameterDto("stop_loss_min_decline", ((Double)tradingStrategyParameters.getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage()).toString()));
        tradingStrategyParameterDtos.add(new StrategyParameterDto("maximum_percentage", ((Double)tradingStrategyParameters.getMaximumPercentage()).toString()));

        tradingStrategyDto.setParameters(tradingStrategyParameterDtos);

        response.setTrading(tradingStrategyDto);
    }

    private void buildCommissionStrategyInformation(GetStrategyInformationResponse response) {
        DynamicCommissionStrategyParameters commissionStrategyParameters = this.tradingConfigurationService.getCommissionStrategyParameters();

        StrategyDto commissionStrategyDto = new StrategyDto();
        commissionStrategyDto.setName("DynamicCommissionStrategy");

        List<StrategyParameterDto> commissionStrategyParameterDtos = new ArrayList<>();

        commissionStrategyParameterDtos.add(new StrategyParameterDto("fixed_amount", commissionStrategyParameters.getFixedAmount().toString()));
        commissionStrategyParameterDtos.add(new StrategyParameterDto("variable_amount_rate", ((Double)commissionStrategyParameters.getVariableAmountRate()).toString()));
        commissionStrategyParameterDtos.add(new StrategyParameterDto("minimum_variable_amount", commissionStrategyParameters.getMinimumVariableAmount().toString()));
        commissionStrategyParameterDtos.add(new StrategyParameterDto("maximum_variable_amount", commissionStrategyParameters.getMaximumVariableAmount().toString()));

        commissionStrategyDto.setParameters(commissionStrategyParameterDtos);

        response.setCommission(commissionStrategyDto);
    }
}
