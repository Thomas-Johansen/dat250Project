package dat250.msd.FeedApp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dat250.msd.FeedApp.model.UserData;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MappingControllerTest {
    @LocalServerPort
    private int port;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private String getBaseURL() {
        return "http://localhost:" + port + "/";
    }
    private String doPostRequest(UserData user){
        RequestBody body = RequestBody.create(gson.toJson(user), JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "user")
                .post(body).build();
        return doRequest(request);
    }
    private String doRequest(Request request) {
        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the todo with the given id.
     */
    private String doGetRequest(String username, String pwd) {
        return this.doGetRequest(getBaseURL() + "user?username=" + username +"&pwd="+pwd);
    }

    /**
     * Gets all todos.
     */
    private String doGetRequest() {
        return this.doGetRequest(getBaseURL() + "user");
    }

    private String doGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return doRequest(request);
    }

    @Test
    void testCreateUser() {
        final UserData user = new UserData();
        user.setUsername("Truls");
        user.setPassword("Password123");
        user.setEmail("TrulsCool@gmail.com");
        final UserData createdUser = gson.fromJson(doPostRequest(user), UserData.class);
        System.out.println("The user_id is "+ createdUser.getUsername());
        assertNotNull(createdUser);
    }
    @Test
    void testGetUser(){
        final UserData user = new UserData();
        user.setUsername("Karl");
        user.setPassword("glhf");
        user.setEmail("KarlMarks@gmail.com");

        final UserData createdUser = gson.fromJson(doPostRequest(user), UserData.class);
        final UserData returnedUser = gson.fromJson(doGetRequest(createdUser.getUsername(),createdUser.getPassword()), UserData.class);
        System.out.println("The user_id is "+ returnedUser.getUsername());

        assertEquals(createdUser.getUsername(),returnedUser.getUsername());
    }
}
