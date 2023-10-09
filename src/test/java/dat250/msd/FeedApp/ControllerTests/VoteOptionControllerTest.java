package dat250.msd.FeedApp.ControllerTests;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.service.FeedAppService;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoteOptionControllerTest {

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
    private String doPostRequest(Long id, String label) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(label),JSON);
        Request request = new Request.Builder()
                .url(getBaseURL() + "vote-option/" + id + "?label=" + label)
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

    private String doGetRequest(long id) throws JsonProcessingException{
        Request request = new Request.Builder()
                .url(getBaseURL() + "vote-option/" + id)
                .get().build();
        return doRequest(request);
    }

    private String doPutRequest(VoteOption option, String label) throws JsonProcessingException {
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(option), JSON);

        Request request = new Request.Builder()
                .url(getBaseURL() + "vote-option?label=" + label)
                .put(body)
                .build();
        return doRequest(request);
    }


    @Test
    void getVoteOptions() throws JsonProcessingException {
        Topic topic = new Topic();
        topic.setName("GetOptions");
        VoteOption option1 = new VoteOption(topic, "Mikal");
        VoteOption option2 = new VoteOption(topic, "Vegard");
        List<VoteOption> options = new ArrayList<VoteOption>();
        options.add(option1);
        options.add(option2);
        topic.setVoteOptions(options);


        feedAppService.getTopicRepository().save(topic);

        long topicID = topic.getId();

        String before = objectMapper.writeValueAsString(options);
        String result = doGetRequest(topicID);

        assertEquals(before, result);
    }

    @Test
    void postVoteOption() throws JsonProcessingException {
        Topic topic = new Topic();
        topic.setName("PostOptions");
        VoteOption option1 = new VoteOption(topic, "Mikal");
        VoteOption option2 = new VoteOption(topic, "Vegard");
        List<VoteOption> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        topic.setVoteOptions(options);
        feedAppService.getTopicRepository().save(topic);

        String option3 = "Thomas";
        long topicID = topic.getId();

        doPostRequest(topicID, option3);
        assertEquals(3, feedAppService.getTopicRepository().getTopicById(topicID).getVoteOptions().size());
    }

    @Test
    void putVoteOption() throws JsonProcessingException {
        Topic topic = new Topic();
        topic.setName("PutOptions");
        VoteOption option1 = new VoteOption(topic, "Mikal");
        VoteOption option2 = new VoteOption(topic, "Vegard");
        List<VoteOption> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        topic.setVoteOptions(options);
        feedAppService.getTopicRepository().save(topic);

        String option3 = "Thomas";

        doPutRequest(option1, option3);

    }
}
