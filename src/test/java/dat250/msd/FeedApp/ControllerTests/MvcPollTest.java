package dat250.msd.FeedApp.ControllerTests;

import static com.jayway.jsonpath.JsonPath.read;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.repository.PollRepository;
import dat250.msd.FeedApp.repository.TopicRepository;
import dat250.msd.FeedApp.repository.UserDataRepository;
import org.junit.jupiter.api.*;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MvcPollTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Topic topic;
    private Poll poll;
    private String sessionId;

    @BeforeAll
    public void setup() throws Exception {
        topic = new Topic();
        topic.setName("Good or Bad?");
        topic.setVoteOptions(List.of(new VoteOption(topic, "Good"), new VoteOption(topic, "Bad")));

        UserData user = new UserData();
        user.setUsername("Peter");
        user.setEmail("outlook@coldmail.com");
        user.setPassword(passwordEncoder.encode("121"));
        user.setTopics(List.of(topic));

        topic.setOwner(user);
        //topic.setPolls(List.of(poll));

        poll = new Poll();
        //poll.setTopic(topic);
        poll.setRoomCode("9999");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.MAX);

        // Save UserData and Topic to DB.
        userDataRepository.save(user);
        topicRepository.save(topic);

        UserData u = new UserData();
        u.setUsername(user.getUsername());
        u.setPassword("121");

        MvcResult response = mockMvc.perform(
                post("/api/login").content(asJsonString(u)).contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn();

        sessionId = read(response.getResponse().getContentAsString(), "$.sessionId");
        System.out.println(sessionId);
    }


    @Test
    @Order(1)
    public void createPoll() throws Exception {
        System.out.println(sessionId);
        MvcResult result = mockMvc.perform(
                        post("/api/poll/" + topic.getId()).header("Authorization", sessionId)
                                .content(asJsonString(poll))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.topic.name").value("Good or Bad?"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.private").value(false)).andReturn();
        poll.setRoomCode(read(result.getResponse().getContentAsString(), "$.roomCode"));
    }

    @Test
    @Order(2)
    public void getPoll() throws Exception {
        mockMvc.perform(get("/api/poll?roomCode=" + poll.getRoomCode()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.topic.name").value("Good or Bad?"));
    }

    @Test
    @Order(3)
    public void putPoll() throws Exception {
        Poll pollToUpdate = new Poll();
        pollToUpdate.setRoomCode("53850380683");
        pollToUpdate.setPrivate(false);
        pollToUpdate.setTopic(topic);
        pollRepository.save(pollToUpdate);

        Poll updatePoll = new Poll();
        updatePoll.setPrivate(true);
        mockMvc.perform(
                        put("/api/poll/" + pollToUpdate.getId()).header("Authorization", sessionId)
                                .content(asJsonString(updatePoll))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.private").value(true));
    }

    @Test
    @Order(4)
    public void deletePoll() throws Exception {
        Poll deletePoll = new Poll();
        deletePoll.setRoomCode("24040");
        deletePoll.setPrivate(true);

        // Set topic will set user to owner as well
        deletePoll.setTopic(topic);

        pollRepository.save(deletePoll);

        mockMvc.perform(
                        delete("/api/poll/" + deletePoll.getId()).header("Authorization", sessionId)
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
