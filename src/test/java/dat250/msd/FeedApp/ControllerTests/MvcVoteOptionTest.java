package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.repository.TopicRepository;
import dat250.msd.FeedApp.repository.UserDataRepository;
import dat250.msd.FeedApp.repository.VoteOptionRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MvcVoteOptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VoteOptionRepository voteOptionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Topic topic;
    private UserData user;
    private Poll poll;
    private String sessionId;

    @BeforeAll
    public void setup() throws Exception {
        topic = new Topic();
        topic.setName("PostOptions");
        topic.setVoteOptions(List.of(new VoteOption(topic, "Mikal"), new VoteOption(topic, "Vegard")));

        user = new UserData();
        user.setUsername("Hans");
        user.setEmail("hans@inmail.no");
        user.setPassword(passwordEncoder.encode("1881"));
        user.setTopics(List.of(topic));

        poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("SoManyVoteOptions");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);
        poll.setPrivate(true);

        topic.setOwner(user);
        topic.setPolls(List.of(poll));

        // Save UserData and Topic to DB.
        userDataRepository.save(user);
        topicRepository.save(topic);

        UserData login = new UserData();
        login.setUsername(user.getUsername());
        login.setPassword("1881");

        MvcResult response = mockMvc.perform(
                post("/api/login").content(asJsonString(login)).contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn();

        sessionId = read(response.getResponse().getContentAsString(), "$.sessionId");
        System.out.println(sessionId);
    }


    @Test
    @Order(1)
    public void createVoteOption() throws Exception {
        mockMvc.perform(
                        post("/api/vote-option/" + topic.getId() + "?label=" + "Thomas")
                                .header("Authorization", sessionId)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.label").value("Thomas"));
    }

    @Test
    @Order(2)
    public void getVoteOption() throws Exception {
        VoteOption voteOption = new VoteOption();
        voteOption.setLabel("Epic label");
        voteOption.setTopic(topic);
        voteOptionRepository.save(voteOption);

        mockMvc.perform(
                        get("/api/vote-option/" + topic.getId())
                                .header("Authorization", sessionId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[3].label").value("Epic label"));
    }

    @Test
    @Order(3)
    public void putVoteOption() throws Exception {
        VoteOption voteOption = new VoteOption(topic, "!!!");
        voteOptionRepository.save(voteOption);
        mockMvc.perform(
                        put("/api/vote-option/" + voteOption.getId() + "?label=Not Epic Label").header("Authorization", sessionId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.label").value("Not Epic Label"));
    }

    @Test
    @Order(4)
    public void deleteVoteOption() throws Exception {
        VoteOption voteOption = new VoteOption(topic, "Delete Label");
        voteOptionRepository.save(voteOption);

        mockMvc.perform(
                        delete("/api/vote-option/" + voteOption.getId()).header("Authorization", sessionId)
                )
                .andDo(print())
                .andExpect(status().isOk());
        assertNull(voteOptionRepository.getVoteOptionById(voteOption.getId()));
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
