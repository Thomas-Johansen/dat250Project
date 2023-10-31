package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.repository.TopicRepository;
import dat250.msd.FeedApp.repository.UserDataRepository;
import dat250.msd.FeedApp.repository.VoteRepository;
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
public class MvcVoteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Topic topic;
    private UserData user;
    private Poll poll;
    private String sessionId;


    @BeforeAll
    public void setup() throws Exception {
        System.out.println("Setup");

        topic = new Topic();
        topic.setName("Vote Topic");
        topic.setVoteOptions(List.of(new VoteOption(topic, "Kame"), new VoteOption(topic, "Hame"), new VoteOption(topic, "Ha")));

        user = new UserData();
        user.setUsername("Geir Arne");
        user.setEmail("Geir@Arne.no");
        user.setPassword(passwordEncoder.encode("515151"));
        user.setTopics(List.of(topic));

        poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("HelloWorld");
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
        login.setPassword("515151");

        MvcResult response = mockMvc.perform(
                post("/api/login").content(asJsonString(login)).contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn();

        sessionId = read(response.getResponse().getContentAsString(), "$.sessionId");
        System.out.println(sessionId);
    }


    @Test
    @Order(1)
    public void createVote() throws Exception {
        VoteOption voteOption = new VoteOption();
        voteOption.setLabel("Hame");

        Vote vote = new Vote();
        vote.setVoteOption(voteOption);
        vote.setPoll(poll);

        mockMvc.perform(
                        post("/api/vote")
                                .header("Authorization", sessionId)
                                .content(asJsonString(vote))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.poll.roomCode").value(poll.getRoomCode()));
    }

    @Test
    @Order(2)
    public void getVotes() throws Exception {
        mockMvc.perform(
                        get("/api/votes?roomCode=" + poll.getRoomCode()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.topicId").value(topic.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalVotes").value(1));
    }

    @Test
    @Order(3)
    public void getVote() throws Exception {
        mockMvc.perform(get("/api/vote?roomCode=" + poll.getRoomCode()).header("Authorization", sessionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.voteOption.label").value("Hame"));
    }

    @Test
    @Order(4)
    public void putVote() throws Exception {
        Vote vote = new Vote();
        vote.setVoter(user);
        vote.setPoll(poll);
        vote.setVoteOption(poll.getTopic().getVoteOptions().get(0));
        voteRepository.save(vote);

        VoteOption newVoteOption = poll.getTopic().getVoteOptions().get(1);
        mockMvc.perform(
                        put("/api/vote/" + vote.getId()).header("Authorization", sessionId)
                                .content(asJsonString(newVoteOption))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.voteOption.label").value("Hame"));
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
