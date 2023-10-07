package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.repository.PollRepository;
import dat250.msd.FeedApp.repository.UserDataRepository;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PollControllerTest {
    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private PollRepository pollRepository;

    @LocalServerPort
    private int port;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getBaseURL() {
        return "http://localhost:" + port + "/";
    }
    private String doPostRequest(UserData userData, Poll poll) throws JsonProcessingException {
        //RequestBody body = RequestBody.create(gson.toJson(poll), JSON);
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(poll),JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "poll?username="+userData.getUsername()+"&pwd="+userData.getPassword())
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

    private String doGetRequest(Long id) {
        return this.doGetRequest(getBaseURL() + "poll"+ "?id="+id);
    }

    private String doGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return doRequest(request);
    }

    @Test
    void testCreatePoll() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Ben");
        user.setPassword("12321");
        user.setEmail("test@gmail.com");
        userDataRepository.save(user);

        Poll poll = new Poll();
        poll.setName("Ben's Amazing Poll");
        poll.setVoteOptions(List.of(new VoteOption(poll,"Cool"),new VoteOption(poll,"Lame")));

        String postResponse = doPostRequest(user,poll);
        System.out.println(postResponse);

        Poll returnedPoll = objectMapper.readValue(postResponse,Poll.class);

        System.out.println("The poll name is "+ returnedPoll.getName());
        //assertNotNull(poll.getName());
        assertEquals("Cool",returnedPoll.getVoteOptions().get(0).getLabel());
    }

    @Test
    void testGetPoll() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Jim");
        user.setPassword("1337");
        user.setEmail("jimsi@mailinator.com");

        Poll poll = new Poll();
        poll.setName("Jim's Not So Amazing Poll");
        poll.setVoteOptions(List.of(new VoteOption(poll,"1"),new VoteOption(poll,"2")));
        poll.setOwner(user);

        user.setPolls(List.of(poll));
        userDataRepository.save(user);

        pollRepository.save(poll);

        String response = doGetRequest(poll.getId());
        System.out.println(response);

        Poll returnedPoll = objectMapper.readValue(response, Poll.class);
        System.out.println("The poll is: "+ returnedPoll.getName());

        assertEquals(poll.getName(),returnedPoll.getName());
    }
}
