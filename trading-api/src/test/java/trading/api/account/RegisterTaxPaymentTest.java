package trading.api.account;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import trading.domain.Amount;
import trading.domain.account.AccountId;
import trading.domain.account.TaxPaymentTransaction;
import trading.domain.account.TransactionId;
import trading.domain.taxes.ProfitCategories;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterTaxPaymentTest extends RegisterTransactionTestBase {
    @Before
    public void initializeRequestBody() throws JSONException {
        this.requestBody = new JSONObject()
                .put("date", "2000-01-02")
                .put("transactionType", "TaxPayment")
                .put("taxPeriodYear", 2019)
                .put("profitCategory", "Sale")
                .put("taxedProfit", 1000.0)
                .put("paidTaxes", 100.0);
    }

    @Test
    public void registerTransaction() throws Exception {
        doAnswer(invocation -> {
            TaxPaymentTransaction transaction = invocation.getArgument(1);

            Assert.assertEquals(LocalDate.of(2000, 1, 2), transaction.getDate());
            Assert.assertEquals(2019, transaction.getTaxPeriodYear());
            Assert.assertEquals(transaction.getProfitCategory(), ProfitCategories.Sale);
            Assert.assertEquals(new Amount(1000.0), transaction.getTaxedProfit());
            Assert.assertEquals(new Amount(100.0), transaction.getPaidTaxes());

            transaction.setId(new TransactionId(100));
            return null;
        }).when(this.accountService).registerTransaction(ArgumentMatchers.eq(new AccountId(1)), any(TaxPaymentTransaction.class));

        this.performRequest()
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId", is(100)));
    }

    @Test
    public void registerTransactionFails_ifTaxPeriodYearNotSpecified() throws Exception {
        this.requestBody.remove("taxPeriodYear");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The tax period year must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifProfitCategoryNotSpecified() throws Exception {
        this.requestBody.remove("profitCategory");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The profit category must be specified.")));
    }

    @Test
    public void registerTransactionFails_forUnknownProfitCategory() throws Exception {
        this.requestBody.put("profitCategory", "abc");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The profit category is unknown.")));
    }

    @Test
    public void registerTransactionFails_ifTaxedProfitNotSpecified() throws Exception {
        this.requestBody.remove("taxedProfit");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The taxed profit must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifPaidTaxesNotSpecified() throws Exception {
        this.requestBody.remove("paidTaxes");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The paid taxes must be specified.")));
    }
}
