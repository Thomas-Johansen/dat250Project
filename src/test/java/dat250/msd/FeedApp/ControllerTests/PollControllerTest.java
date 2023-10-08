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

    private String doGetRequest(Long id) {
        Request request = new Request.Builder()
                .url(getBaseURL() + "poll/"+id)
                .get()
                .build();
        return doRequest(request);
    }

    private String doPutRequest(Long id, UserData user, Poll poll) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(poll),JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "poll/"+id+"?username="+user.getUsername()+"&pwd="+user.getPassword())
                .put(body)
                .build();
        return doRequest(request);
    }

    private String doDeleteRequest(Long id, UserData user) {
        Request request = new Request.Builder()
                .url(getBaseURL() + "poll/"+id+"?username="+user.getUsername()+"&pwd="+user.getPassword())
                .delete()
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
        final Topic topic = new Topic();
        topic.setName("Henry's Popularity Vote");
        topic.setVoteOptions(List.of(new VoteOption(topic,"Amazing!"),new VoteOption(topic,"Who?"),new VoteOption(topic,"Fell off.")));

        final Poll poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("3333");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);

        final UserData user = new UserData();
        user.setUsername("Henry");
        user.setEmail("he1@ea.com");
        user.setPassword("1111");
        user.setTopics(List.of(topic));

        topic.setOwner(user);
        topic.setPolls(List.of(poll));

        userDataRepository.save(user);
        topicRepository.save(topic);

        String postResponse = doGetRequest(poll.getRoomCode());

        System.out.println(postResponse);

        Poll returnedPoll = objectMapper.readValue(postResponse, Poll.class);
        assertEquals(topic.getName(), returnedPoll.getTopic().getName());
        assertEquals(poll.getRoomCode(),returnedPoll.getRoomCode());
        assertNull(returnedPoll.getTopic().getOwner());
    }

    @Test
    void testPutPoll() throws JsonProcessingException {
        final Topic topic = new Topic();
        topic.setName("Henry's who's Vote");
        topic.setVoteOptions(List.of(new VoteOption(topic,"Who!"),new VoteOption(topic,"Who?"),new VoteOption(topic,"Who.")));

        final Poll poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("3456");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);

        final UserData user = new UserData();
        user.setUsername("Henry The 2nd");
        user.setEmail("he2@ea.com");
        user.setPassword("1111");
        user.setTopics(List.of(topic));

        topic.setOwner(user);
        topic.setPolls(List.of(poll));

        userDataRepository.save(user);
        topicRepository.save(topic);

        Poll updatePoll = new Poll();
        updatePoll.setRoomCode("1881");
        updatePoll.setStartDate(LocalDateTime.MIN);
        updatePoll.setEndDate(LocalDateTime.MIN);

        String postResponse = doPutRequest(poll.getId(),user,updatePoll);
        System.out.println(postResponse);

        Poll returnedPoll = objectMapper.readValue(postResponse, Poll.class);
        assertEquals(topic.getName(), returnedPoll.getTopic().getName());
        assertEquals(poll.getRoomCode(),returnedPoll.getRoomCode());
        assertNull(returnedPoll.getTopic().getOwner());

        // Check updated values
        assertEquals(LocalDateTime.MIN,returnedPoll.getStartDate());
        assertEquals(LocalDateTime.MIN,returnedPoll.getEndDate());

        // RoomCode should remain unchanged
        assertEquals("3456",returnedPoll.getRoomCode());
    }

    @Test
    void testDeletePoll() throws JsonProcessingException {
        final Topic topic = new Topic();
        topic.setName("Henry's who's Vote");
        topic.setVoteOptions(List.of(new VoteOption(topic,"Who!"),new VoteOption(topic,"Who?"),new VoteOption(topic,"Who.")));

        final Poll poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("7890");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);

        final UserData user = new UserData();
        user.setUsername("Henry The 3nd");
        user.setEmail("he3@ea.com");
        user.setPassword("1111");
        user.setTopics(List.of(topic));

        topic.setOwner(user);
        topic.setPolls(List.of(poll));

        userDataRepository.save(user);
        topicRepository.save(topic);

        String postResponse = doDeleteRequest(poll.getId(),user);
        System.out.println(postResponse);

        Poll returnedPoll = objectMapper.readValue(postResponse, Poll.class);
        assertEquals(topic.getName(), returnedPoll.getTopic().getName());

        // See if it got removed from DB, should get 404
        String getResponse = doGetRequest(poll.getId());
        assertEquals(0,getResponse.length());
    }
}
