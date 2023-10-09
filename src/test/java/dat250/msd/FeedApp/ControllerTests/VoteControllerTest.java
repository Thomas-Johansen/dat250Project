package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
        String req = objectMapper.writeValueAsString(vote);
        RequestBody body = RequestBody.create(req, JSON);
        Request request = new Request.Builder()
                .url(getBaseURL() + "vote")
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

    private String doGetRequest(String roomCode) {
        Request request = new Request.Builder()
                .url(getBaseURL() + "vote?roomCode=" + roomCode)
                .get()
                .build();
        return doRequest(request);
    }

    private String doPutRequest(Vote vote, VoteOption option) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(vote),JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "vote?id="+option)
                .put(body)
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
        poll.setEndDate(LocalDateTime.MAX);

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

        assertEquals("KameHameHa!",createdVote.getVoteOption().getLabel());
        assertNotNull(createdVote);
    }

    @Test
    void getVote() throws JsonProcessingException {
        final Topic topic = new Topic();
        final Poll poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("epic");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);

        final UserData user = new UserData();
        user.setUsername("Mr.Get Votes");
        user.setEmail("t@bt.eu");
        user.setPassword("321");

        topic.setPolls(List.of(poll));
        topic.setVoteOptions(List.of(new VoteOption(topic,"YES YES YES"), new VoteOption(topic, "NO NO NO")));

        feedAppService.getUserDataRepository().save(user);
        feedAppService.getTopicRepository().save(topic);

        // Check that there are no votes
        String res = doGetRequest(poll.getRoomCode());
        List<Vote> returnedVotes = objectMapper.readValue(res, new TypeReference<>(){});
        assertEquals(returnedVotes.size(),0);

        // Add a new Vote
        final Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setVoter(user);
        vote.setVoteOption(topic.getVoteOptions().get(0));
        feedAppService.getVoteRepository().save(vote);

        // Test newly created Vote
        String response = doGetRequest(poll.getRoomCode());
        System.out.println(response);
        returnedVotes = objectMapper.readValue(response, new TypeReference<>() {});
        assertEquals(1, returnedVotes.size());
        assertEquals("YES YES YES", returnedVotes.get(0).getVoteOption().getLabel());
        assertNull(returnedVotes.get(0).getVoter().getPassword());
    }
    @Test
    void updateVote() throws JsonProcessingException {
        final Topic topic = new Topic();
        final Poll poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("cool");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);

        final UserData user = new UserData();
        user.setUsername("Mr.Put Votes");
        user.setEmail("t@et.eu");
        user.setPassword("123");

        topic.setPolls(List.of(poll));
        VoteOption option1 = new VoteOption(topic,"YES YES YES");
        VoteOption option2 = new VoteOption(topic, "NO NO NO");
        topic.setVoteOptions(List.of(option1, option2));

        feedAppService.getUserDataRepository().save(user);
        feedAppService.getTopicRepository().save(topic);

        // Check that there are no votes
        String res = doGetRequest(poll.getRoomCode());
        List<Vote> returnedVotes = objectMapper.readValue(res, new TypeReference<>(){});
        assertEquals(returnedVotes.size(),0);

        // Add a new Vote
        final Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setVoter(user);
        vote.setVoteOption(topic.getVoteOptions().get(0));
        feedAppService.getVoteRepository().save(vote);

        // Test newly created Vote
        String response = doGetRequest(poll.getRoomCode());
        System.out.println(response);
        returnedVotes = objectMapper.readValue(response, new TypeReference<>() {});
        assertEquals(1, returnedVotes.size());
        assertEquals("YES YES YES", returnedVotes.get(0).getVoteOption().getLabel());
        Vote updatedVote = objectMapper.readValue(doPutRequest(returnedVotes.get(0), option2), new TypeReference<>() {
        });
        assertEquals("YES YES YES", updatedVote.getVoteOption().getLabel());
        assertNull(updatedVote.getVoter().getPassword());
    }
}

