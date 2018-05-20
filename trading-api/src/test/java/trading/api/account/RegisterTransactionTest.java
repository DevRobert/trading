package trading.api.account;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import trading.domain.*;
import trading.domain.account.AccountId;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterTransactionTest extends AccountControllerTestBase {
    private JSONObject requestBody;

    @Before
    public void initializeRequestBody() throws JSONException {
        this.requestBody = new JSONObject()
                .put("transactionType", "Buy")
                .put("isin", "A")
                .put("quantity", 5)
                .put("totalPrice", 1000.0)
                .put("commission", 20.0);
    }

    private ResultActions performRequest() throws Exception {
        return this.mvc.perform(MockMvcRequestBuilders
                .post("/api/account/transactions/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void registerTransaction() throws Exception {
        doAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(1);

            Assert.assertEquals(TransactionType.Buy, transaction.getTransactionType());
            Assert.assertEquals("A", transaction.getIsin().getText());
            Assert.assertEquals(new Quantity(5), transaction.getQuantity());
            Assert.assertEquals(new Amount(1000.0), transaction.getTotalPrice());
            Assert.assertEquals(new Amount(20.0), transaction.getCommission());

            transaction.setId(new TransactionId(100));
            return null;
        }).when(this.accountService).registerTransaction(ArgumentMatchers.eq(new AccountId(1)), any(Transaction.class));

        this.performRequest()
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactionId", is(100)));
    }

    @Test
    public void registerTransactionFails_ifTransactionTypeNotSpecified() throws Exception {
        this.requestBody.remove("transactionType");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The transaction type must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifTransactionTypeEmpty() throws Exception {
        this.requestBody.put("transactionType", "");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The transaction type must not be empty.")));
    }

    @Test
    public void registerTransactionFails_ifTransactionTypeInvalid() throws Exception {
        this.requestBody.put("transactionType", "InvalidType");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The transaction type 'InvalidType' is invalid.")));
    }

    @Test
    public void registerTransactionFails_ifIsinNotSpecified() throws Exception {
        this.requestBody.remove("isin");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The ISIN must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifIsinEmpty() throws Exception {
        this.requestBody.put("isin", "");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The ISIN must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifIsinUnknown() throws Exception {
        this.requestBody.put("isin", "UnknownISIN");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The given ISIN is unknown.")));
    }

    @Test
    public void registerTransactionFails_ifQuantityNotSpecified() throws Exception {
        this.requestBody.remove("quantity");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The quantity must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifTotalPriceNotSpecified() throws Exception {
        this.requestBody.remove("totalPrice");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The total price must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifCommissionNotSpecified() throws Exception {
        this.requestBody.remove("commission");

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("The commission must be specified.")));
    }

    @Test
    public void registerTransactionFails_ifDomainRuleViolated() throws Exception {
        doAnswer(invocation -> {
            throw new DomainException("Example for domain exception.");
        }).when(this.accountService).registerTransaction(ArgumentMatchers.eq(new AccountId(1)), any(Transaction.class));

        this.performRequest()
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("message", is("Example for domain exception.")));
    }
}
