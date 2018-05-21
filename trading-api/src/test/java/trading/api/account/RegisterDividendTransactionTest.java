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
import trading.domain.account.DividendTransaction;
import trading.domain.account.TransactionId;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterDividendTransactionTest extends RegisterTransactionTestBase {
    @Before
    public void initializeRequestBody() throws JSONException {
        this.requestBody = new JSONObject()
                .put("date", "2000-01-02")
                .put("transactionType", "Dividend")
                .put("isin", "A")
                .put("amount", 1000.0);
    }

    @Test
    public void registerTransaction() throws Exception {
        doAnswer(invocation -> {
            DividendTransaction transaction = invocation.getArgument(1);

            Assert.assertEquals(LocalDate.of(2000, 1, 2), transaction.getDate());
            Assert.assertEquals("A", transaction.getIsin().getText());
            Assert.assertEquals(new Amount(1000.0), transaction.getAmount());

            transaction.setId(new TransactionId(100));
            return null;
        }).when(this.accountService).registerTransaction(ArgumentMatchers.eq(new AccountId(1)), any(DividendTransaction.class));

        this.performRequest()
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId", is(100)));
    }

    @Test
    public void registerTransactionFails_ifDateNotSpecified() throws Exception {
        this.requestBody.remove("date");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The date must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifIsinNotSpecified() throws Exception {
        this.requestBody.remove("isin");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The ISIN must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifAmountNotSpecified() throws Exception {
        this.requestBody.remove("amount");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The amount must be specified.")));
    }
}
