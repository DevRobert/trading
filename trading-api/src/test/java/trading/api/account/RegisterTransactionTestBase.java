package trading.api.account;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public abstract class RegisterTransactionTestBase extends AccountControllerTestBase {
    protected JSONObject requestBody;

    protected ResultActions performRequest() throws Exception {
        return this.mvc.perform(MockMvcRequestBuilders
                .post("/api/account/transactions/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.toString())
                .accept(MediaType.APPLICATION_JSON));
    }
}
