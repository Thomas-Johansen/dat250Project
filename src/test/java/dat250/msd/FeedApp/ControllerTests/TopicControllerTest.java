package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TopicControllerTest {
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
    private String doPostRequest(UserData userData, Topic topic) throws JsonProcessingException {
        //RequestBody body = RequestBody.create(gson.toJson(topic), JSON);
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(topic),JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "topic?username="+userData.getUsername()+"&pwd="+userData.getPassword())
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

    private String doGetRequest(Long id) {
        Request request = new Request.Builder()
                .url(getBaseURL() + "topic"+ "/"+id)
                .get()
                .build();
        return doRequest(request);
    }

    private String doPutRequest(Long id, UserData user, List<VoteOption> newVoteOption) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(newVoteOption),JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "topic"+ "/"+id+"?username="+user.getUsername()+"&pwd="+user.getPassword())
                .put(body)
                .build();
        return doRequest(request);
    }

    private String doDeleteRequest(Long id, UserData user) {
        Request request = new Request.Builder()
                .url(getBaseURL() + "topic"+ "/"+id+"?username="+user.getUsername()+"&pwd="+user.getPassword())
                .delete()
                .build();
        return doRequest(request);
    }

    @Test
    void testCreateTopic() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Ben");
        user.setPassword("12321");
        user.setEmail("test@gmail.com");
        userDataRepository.save(user);

        Topic topic = new Topic();
        topic.setName("Ben's Amazing Topic");
        topic.setVoteOptions(List.of(new VoteOption(topic,"Cool"),new VoteOption(topic,"Lame")));

        String postResponse = doPostRequest(user, topic);
        System.out.println(postResponse);

        Topic returnedTopic = objectMapper.readValue(postResponse, Topic.class);

        System.out.println("The topic name is "+ returnedTopic.getName());
        //assertNotNull(topic.getName());
        assertEquals("Cool", returnedTopic.getVoteOptions().get(0).getLabel());
    }

    @Test
    void testGetTopic() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Jim");
        user.setPassword("1337");
        user.setEmail("jimsi@mailinator.com");

        Topic topic = new Topic();
        topic.setName("Jim's Not So Amazing Topic");
        topic.setVoteOptions(List.of(new VoteOption(topic,"1"),new VoteOption(topic,"2")));
        topic.setOwner(user);

        user.setTopics(List.of(topic));

        userDataRepository.save(user);
        topicRepository.save(topic);

        String response = doGetRequest(topic.getId());
        System.out.println(response);

        Topic returnedTopic = objectMapper.readValue(response, Topic.class);
        System.out.println("The topic is: "+ returnedTopic.getName());

        assertEquals(topic.getName(), returnedTopic.getName());
    }

    @Test
    void testPutTopic() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Hans");
        user.setPassword("5555");
        user.setEmail("Han@gmr.com");

        Topic topic = new Topic();
        topic.setName("Indecisive Topic");
        topic.setVoteOptions(List.of(new VoteOption(topic,"L1"),new VoteOption(topic,"L2")));
        topic.setOwner(user);

        user.setTopics(List.of(topic));

        userDataRepository.save(user);
        topicRepository.saveAndFlush(topic);

        VoteOption newVoteOption = new VoteOption();
        newVoteOption.setLabel("L3");

        String response = doPutRequest(topic.getId(),user,List.of(newVoteOption));
        System.out.println(response);

        Topic returnedTopic = objectMapper.readValue(response, Topic.class);
        System.out.println("The topic is: "+ returnedTopic.getName());

        assertEquals(topic.getName(), returnedTopic.getName());
        assertEquals(1,returnedTopic.getVoteOptions().size());
    }

    @Test
    void testDeleteTopic() throws JsonProcessingException {
        final UserData user = new UserData();
        user.setUsername("Mac");
        user.setPassword("HEYO");
        user.setEmail("mac@gmail.com");

        Topic topic = new Topic();
        topic.setName("YES");
        topic.setVoteOptions(List.of(new VoteOption(topic,"YES!"),new VoteOption(topic,"Yes.")));
        topic.setOwner(user);

        user.setTopics(List.of(topic));

        userDataRepository.save(user);
        topicRepository.saveAndFlush(topic);

        String response = doDeleteRequest(topic.getId(),user);
        System.out.println(response);

        Topic returnedTopic = objectMapper.readValue(response, Topic.class);
        System.out.println("The topic is: "+ returnedTopic.getName());

        assertEquals(topic.getName(), returnedTopic.getName());
        assertEquals(2,returnedTopic.getVoteOptions().size());

        // See if it got removed from DB, should get 404
        String getResponse = doGetRequest(topic.getId());
        assertEquals(0,getResponse.length());
    }
}
