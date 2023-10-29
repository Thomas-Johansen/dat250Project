package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.repository.TopicRepository;
import dat250.msd.FeedApp.repository.UserDataRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static com.jayway.jsonpath.JsonPath.read;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MvcTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Topic topic;
    private UserData user;
    private String sessionId;

    @BeforeAll
    public void setup() throws Exception {
        System.out.println("Setup");

        topic = new Topic();
        topic.setName("Ben's Amazing Topic?");
        topic.setVoteOptions(List.of(new VoteOption(topic,"Good"), new VoteOption(topic, "Bad")));

        user = new UserData();
        user.setUsername("Ben?");
        user.setEmail("inlook@coldmail.com");
        user.setPassword(passwordEncoder.encode("123567"));
        user.setTopics(List.of(topic));

        Poll poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("20000");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);

        topic.setOwner(user);
        topic.setPolls(List.of(poll));

        // Save UserData and Topic to DB.
        userDataRepository.save(user);
        topicRepository.save(topic);

        UserData u = new UserData();
        u.setUsername(user.getUsername());
        u.setPassword("123567");

        MvcResult response = mockMvc.perform(
                post("/api/login").content(asJsonString(u)).contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn();

        sessionId = read(response.getResponse().getContentAsString(), "$.sessionId");
        System.out.println(sessionId);
    }


    @Test
    @Order(1)
    public void createTopic() throws Exception {
        mockMvc.perform(
                    post("/api/topic").header("Authorization",sessionId)
                            .content(asJsonString(topic))
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(topic.getName()));
    }

    @Test
    @Order(2)
    public void getTopic() throws Exception{
        mockMvc.perform(get("/api/topic/"+topic.getId()).header("Authorization",sessionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(topic.getName()));
    }

    @Test
    @Order(3)
    public void putTopic() throws Exception{
        List<VoteOption> voteOptions = List.of(new VoteOption(null,"no u"),new VoteOption(null,"heyo"));
        mockMvc.perform(
                        put("/api/topic/" + topic.getId()).header("Authorization",sessionId)
                                .content(asJsonString(voteOptions))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(topic.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.voteOptions[0].label").value("no u"));
    }

    @Test
    @Order(4)
    public void deleteTopic() throws Exception{
        Topic deleteTopic = new Topic();
        deleteTopic.setName("Delete this Topic");
        deleteTopic.setOwner(user);

        topicRepository.save(deleteTopic);

        mockMvc.perform(
                        delete("/api/topic/" + deleteTopic.getId()).header("Authorization",sessionId)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
