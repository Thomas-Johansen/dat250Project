package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.repository.TopicRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PollControllerTest {
    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private TopicRepository topicRepository;

    @LocalServerPort
    private int port;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private String getBaseURL() {
        return "http://localhost:" + port + "/";
    }
    private String doPostRequest(Long topicId, UserData userData, Poll poll) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(poll),JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "poll/"+topicId+"?username="+userData.getUsername()+"&pwd="+userData.getPassword())
                .post(body).build();

        System.out.println(request.url());
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

    private String doGetRequest(String roomCode) {
        Request request = new Request.Builder()
                .url(getBaseURL() + "poll?roomCode="+roomCode)
                .get()
                .build();
        return doRequest(request);
    }

    @Test
    void testCreatePoll() throws JsonProcessingException {
        final Topic topic = new Topic();
        topic.setName("Good or Bad?");

        final Poll poll = new Poll();
        //poll.setTopic(topic);
        poll.setRoomCode("2902");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);

        final UserData user = new UserData();
        user.setUsername("Peter");
        user.setEmail("outlook@coldmail.com");
        user.setPassword("121");
        user.setTopics(List.of(topic));

        topic.setOwner(user);

        //topic.setPolls(List.of(poll));
        topic.setVoteOptions(List.of(new VoteOption(topic,"Good"), new VoteOption(topic, "Bad")));

        userDataRepository.save(user);
        topicRepository.save(topic);

        String postResponse = doPostRequest(topic.getId(),user, poll);
        System.out.println(postResponse);

        Poll returnedPoll = objectMapper.readValue(postResponse, Poll.class);
        assertEquals("Good or Bad?", returnedPoll.getTopic().getName());
        assertEquals("2902",returnedPoll.getRoomCode());
        assertNull(returnedPoll.getTopic().getOwner());
    }

    @Test
    void testGetPoll() throws JsonProcessingException {
        // TODO
    }
}
