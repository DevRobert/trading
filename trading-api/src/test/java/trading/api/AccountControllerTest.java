package trading.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getTransactions() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/api/accounts/2/transactions/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("Test"));
    }

    @Test
    public void getTransaction() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/api/accounts/2/transactions/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("Test"));
    }

    @Test
    public void getPositions() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/api/accounts/2/positions/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("Test"));
    }

    @Test
    public void getPosition() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/api/accounts/2/positions/DE0008430026").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("Test"));
    }
}
