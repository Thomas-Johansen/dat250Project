package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.service.FeedAppService;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class VoteControllerTest {
    @LocalServerPort
    private int port;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private FeedAppService feedAppService;

    private String getBaseURL() {
        return "http://localhost:" + port + "/";
    }
    private String doPostRequest(Vote vote) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(vote), JSON);
        Request request = new Request.Builder()
                .url(getBaseURL() + "vote")
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
        return this.doGetRequest(getBaseURL() + "vote?id=" + id);
    }

    private String doGetRequest() {
        return this.doGetRequest(getBaseURL() + "vote");
    }

    private String doGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return doRequest(request);
    }

    @Test
    void createVote() throws JsonProcessingException {
        final Vote vote = new Vote();
        final Topic topic = new Topic();
        final Poll poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("HelloWorld!");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.now());

        final VoteOption option = new VoteOption(topic,"KameHameHa!");

        final UserData user = new UserData();
        user.setUsername("GeirArne");
        user.setEmail("Geir@Arne.no");
        user.setPassword("123");

        vote.setPoll(poll);
        vote.setVoter(user);
        vote.setVoteOption(option);

        topic.setPolls(List.of(poll));
        topic.setVoteOptions(List.of(option));

        feedAppService.getUserDataRepository().save(user);
        feedAppService.getTopicRepository().save(topic);

        final Vote createdVote = objectMapper.readValue(doPostRequest(vote), Vote.class);
        System.out.println("The vote_id is "+ createdVote.getId());

        assertEquals(createdVote.getVoteOption().getLabel(), "KameHameHa!");
        assertNotNull(createdVote);
    }
}

