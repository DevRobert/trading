package trading.api.account;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerTest extends AccountControllerTestBase {

    @Test
    public void getPositions() throws Exception {
        // Available money
        // 10,000
        // - 1,000 - 20 (A)
        // - 1,000 - 20 (B)
        // + 100 (Dividend)
        // = 8060
        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/account/positions/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("positions", hasSize(2)))
                .andExpect(jsonPath("positions[0].isin", is("A")))
                .andExpect(jsonPath("positions[0].name", is("Company A")))
                .andExpect(jsonPath("positions[0].quantity", is(10)))
                .andExpect(jsonPath("positions[0].marketPrice", is(200.0)))
                .andExpect(jsonPath("positions[0].totalMarketPrice", is(2000.0)))
                .andExpect(jsonPath("positions[1].isin", is("B")))
                .andExpect(jsonPath("positions[1].name", is("Unknown")))
                .andExpect(jsonPath("positions[1].quantity", is(10)))
                .andExpect(jsonPath("positions[1].marketPrice", is(300.0)))
                .andExpect(jsonPath("positions[1].totalMarketPrice", is(3000.0)))
                .andExpect(jsonPath("summary.totalStocksQuantity", is(20)))
                .andExpect(jsonPath("summary.totalStocksMarketPrice", is(5000.0)))
                .andExpect(jsonPath("summary.availableMoney", is(8060.0)))
                .andExpect(jsonPath("summary.totalBalance", is(13060.0)))
                .andExpect(jsonPath("marketPricesDate", is("2018-05-09")));
    }

    /*
    @Test
    public void getPosition() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/account/positions/DE0008430026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("Test"));
    }
    */

    @Test
    public void getTransactions() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/account/transactions/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("transactions", hasSize(3)))
                .andExpect(jsonPath("transactions[0].date", is("2018-04-12")))
                .andExpect(jsonPath("transactions[0].transactionType", is("Buy")))
                .andExpect(jsonPath("transactions[0].isin", is("A")))
                .andExpect(jsonPath("transactions[0].name", is("Company A")))
                .andExpect(jsonPath("transactions[0].quantity", is(10)))
                .andExpect(jsonPath("transactions[0].marketPrice", is(100.0)))
                .andExpect(jsonPath("transactions[0].totalPrice", is(1000.0)))
                .andExpect(jsonPath("transactions[0].commission", is(20.0)))
                .andExpect(jsonPath("transactions[0].amount").doesNotExist())
                .andExpect(jsonPath("transactions[1].date", is("2018-04-13")))
                .andExpect(jsonPath("transactions[1].transactionType", is("Buy")))
                .andExpect(jsonPath("transactions[1].isin", is("B")))
                .andExpect(jsonPath("transactions[1].name", is("Unknown")))
                .andExpect(jsonPath("transactions[1].quantity", is(10)))
                .andExpect(jsonPath("transactions[1].marketPrice", is(100.0)))
                .andExpect(jsonPath("transactions[1].totalPrice", is(1000.0)))
                .andExpect(jsonPath("transactions[1].commission", is(20.0)))
                .andExpect(jsonPath("transactions[1].amount").doesNotExist())
                .andExpect(jsonPath("transactions[2].date", is("2018-04-14")))
                .andExpect(jsonPath("transactions[2].transactionType", is("Dividend")))
                .andExpect(jsonPath("transactions[2].isin", is("B")))
                .andExpect(jsonPath("transactions[2].name", is("Unknown")))
                .andExpect(jsonPath("transactions[2].quantity").doesNotExist())
                .andExpect(jsonPath("transactions[2].marketPrice").doesNotExist())
                .andExpect(jsonPath("transactions[2].totalPrice").doesNotExist())
                .andExpect(jsonPath("transactions[2].commission").doesNotExist())
                .andExpect(jsonPath("transactions[2].amount", is(100.0)));
    }

    /*
    @Test
    public void getTransaction() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders
                .get("/api/account/transactions/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("Test"));
    }
    */
}
