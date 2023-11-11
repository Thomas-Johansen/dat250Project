package dat250.msd.FeedApp.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat250.msd.FeedApp.dto.RegisterDTO;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MvcUserTest {

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
    private Poll poll;
    private String sessionId;


    @BeforeAll
    public void setup() throws Exception {
        topic = new Topic();
        topic.setName("Topico");
        topic.setVoteOptions(List.of(new VoteOption(topic, "...")));

        user = new UserData();
        user.setUsername("John Impact");
        user.setEmail("john@gmail.com");
        user.setPassword(passwordEncoder.encode("999"));
        user.setTopics(List.of(topic));

        poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("UserRoom");
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
        login.setPassword("999");

        MvcResult response = mockMvc.perform(
                post("/api/login")
                        .content(asJsonString(login))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print()).andReturn();

        sessionId = read(response.getResponse().getContentAsString(), "$.sessionId");
        System.out.println(sessionId);
    }


    @Test
    @Order(1)
    public void createUser() throws Exception {
        RegisterDTO newUser = new RegisterDTO();
        newUser.setUsername("Peter The Amazing");
        newUser.setEmail("pet3r@4mzing.com");
        newUser.setPassword("123");

        mockMvc.perform(
                        post("/api/user").header("Authorization", sessionId)
                                .content(asJsonString(newUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(newUser.getUsername()));
    }

    @Test
    @Order(2)
    public void getUser() throws Exception {
        mockMvc.perform(
                        get("/api/user")
                                .header("Authorization", sessionId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    @Order(3)
    public void putUser() throws Exception {
        UserData updateUser = new UserData();
        updateUser.setPassword("12345");
        updateUser.setEmail("new@mail.com");

        mockMvc.perform(
                        put("/api/user")
                                .header("Authorization", sessionId)
                                .content(asJsonString(updateUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updateUser.getEmail()));
    }

    @Test
    @Order(4)
    public void deleteUser() throws Exception {
        mockMvc.perform(
                        delete("/api/user").header("Authorization", sessionId)
                )
                .andDo(print())
                .andExpect(status().isOk());
        assertNull(userDataRepository.getUserDataByUsername("John Impact"));
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
