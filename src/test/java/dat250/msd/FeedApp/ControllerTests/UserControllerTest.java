package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dat250.msd.FeedApp.model.UserData;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @LocalServerPort
    private int port;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseURL() {
        return "http://localhost:" + port + "/";
    }
    private String doPostRequest(UserData user) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(user), JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "user")
                .post(body).build();
        return doRequest(request);
    }
    private String doRequest(Request request) {
        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.code());
            System.out.println(response.headers());
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String doGetRequest(String username, String pwd) {
        return this.doGetRequest(getBaseURL() + "user?username=" + username +"&pwd="+pwd);
    }

    private String doGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return doRequest(request);
    }

    private String doDeleteRequest(UserData user) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(user), JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "user")
                .delete(body)
                .build();
        return doRequest(request);
    }

    @Test
    void testCreateUser() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Truls");
        user.setPassword("Password123");
        user.setEmail("TrulsCool@gmail.com");
        final UserData createdUser = objectMapper.readValue(doPostRequest(user), UserData.class);
        System.out.println("The user_id is "+ createdUser.getUsername());
        assertNotNull(createdUser);
    }
    @Test
    void testGetUser() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Karl");
        user.setPassword("glhf");
        user.setEmail("KarlMarks@gmail.com");

        final UserData createdUser = objectMapper.readValue(doPostRequest(user), UserData.class);
        final UserData returnedUser = objectMapper.readValue(doGetRequest(createdUser.getUsername(),createdUser.getPassword()), UserData.class);
        System.out.println("The user_id is "+ returnedUser.getUsername());

        assertEquals(createdUser.getUsername(),returnedUser.getUsername());
    }

    @Test
    void testDeleteUser() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Karl Den Store");
        user.setPassword("glhf");
        user.setEmail("KarlMarks2@gmail.com");

        final UserData createdUser = objectMapper.readValue(doPostRequest(user), UserData.class);

        final UserData returnedUser = objectMapper.readValue(doGetRequest(createdUser.getUsername(),createdUser.getPassword()), UserData.class);
        assertNotNull(returnedUser);

        String deletedUser = doDeleteRequest(user);
        assertEquals(0,deletedUser.length());

        String getEmptyUser = doGetRequest(createdUser.getUsername(),createdUser.getPassword());
        assertEquals(0,getEmptyUser.length());
    }
}
