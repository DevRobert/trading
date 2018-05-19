package trading.api;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import trading.application.TradingConfigurationService;
import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.broker.DynamicCommissionStrategyParameters;
import trading.domain.broker.DynamicCommissionStrategyParametersBuilder;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategyParameters;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StrategyControllerTest extends ControllerTestBase {
    @MockBean
    private TradingConfigurationService tradingConfigurationService;

    @Test
    public void getStrategyInformation() throws Exception {
        DayCount buyTriggerLocalMaximumLookBehindPeriod = new DayCount(2);
        double buyTriggerMinDeclineFromLocalMaximumPercentage= 0.1;
        double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = 0.2;
        double activateTrailingStopLossMinRaiseSinceBuyingPercentage = 0.3;
        double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = 0.4;
        double maximumPercentage = 0.5;

        CompoundLocalMaximumTradingStrategyParameters tradingStrategyParameters = new CompoundLocalMaximumTradingStrategyParameters(
                buyTriggerLocalMaximumLookBehindPeriod,
                buyTriggerMinDeclineFromLocalMaximumPercentage,
                sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage,
                activateTrailingStopLossMinRaiseSinceBuyingPercentage,
                sellTriggerStopLossMinimumDeclineSinceBuyingPercentage,
                maximumPercentage
        );

        given(this.tradingConfigurationService.getTradingStrategyParameters()).willReturn(tradingStrategyParameters);

        DynamicCommissionStrategyParameters commissionStrategyParameters = new DynamicCommissionStrategyParametersBuilder()
                .setFixedAmount(new Amount(10))
                .setVariableAmountRate(0.1)
                .setMinimumVariableAmount(new Amount(5))
                .setMaximumVariableAmount(new Amount(50))
                .build();

        given(this.tradingConfigurationService.getCommissionStrategyParameters()).willReturn(commissionStrategyParameters);

        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/strategy")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("trading.name", is("CompoundLocalMaximumTradingStrategy")))
                .andExpect(jsonPath("trading.parameters", hasSize(6)))
                .andExpect(jsonPath("trading.parameters[0].name", is("maximum_look_behind")))
                .andExpect(jsonPath("trading.parameters[0].value", is("2")))
                .andExpect(jsonPath("trading.parameters[1].name", is("maximum_min_decline")))
                .andExpect(jsonPath("trading.parameters[1].value", is("0.1")))
                .andExpect(jsonPath("trading.parameters[2].name", is("trailing_stop_loss_min_decline")))
                .andExpect(jsonPath("trading.parameters[2].value", is("0.2")))
                .andExpect(jsonPath("trading.parameters[3].name", is("trailing_stop_loss_activation_min_raise")))
                .andExpect(jsonPath("trading.parameters[3].value", is("0.3")))
                .andExpect(jsonPath("trading.parameters[4].name", is("stop_loss_min_decline")))
                .andExpect(jsonPath("trading.parameters[4].value", is("0.4")))
                .andExpect(jsonPath("trading.parameters[5].name", is("maximum_percentage")))
                .andExpect(jsonPath("trading.parameters[5].value", is("0.5")))
                .andExpect(jsonPath("commission.name", is("DynamicCommissionStrategy")))
                .andExpect(jsonPath("commission.parameters", hasSize(4)))
                .andExpect(jsonPath("commission.parameters[0].name", is("fixed_amount")))
                .andExpect(jsonPath("commission.parameters[0].value", is("10.0")))
                .andExpect(jsonPath("commission.parameters[1].name", is("variable_amount_rate")))
                .andExpect(jsonPath("commission.parameters[1].value", is("0.1")))
                .andExpect(jsonPath("commission.parameters[2].name", is("minimum_variable_amount")))
                .andExpect(jsonPath("commission.parameters[2].value", is("5.0")))
                .andExpect(jsonPath("commission.parameters[3].name", is("maximum_variable_amount")))
                .andExpect(jsonPath("commission.parameters[3].value", is("50.0")));
    }
}
