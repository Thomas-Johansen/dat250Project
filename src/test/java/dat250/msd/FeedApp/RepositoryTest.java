package dat250.msd.FeedApp;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.repository.PollRepository;
import dat250.msd.FeedApp.repository.TopicRepository;
import dat250.msd.FeedApp.repository.UserDataRepository;
import dat250.msd.FeedApp.repository.VoteRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryTest {

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    private UserData testUser;
    private Topic topic;
    private Poll poll;

    @BeforeAll
    public void setup(){
        testUser = new UserData();
        testUser.setUsername("John Testing");
        testUser.setPassword("123");
        testUser.setEmail("john@test.com");

        topic = new Topic();
        topic.setName("Test Poll");
        topic.setOwner(testUser);

        VoteOption optionYes = new VoteOption(topic, "Yes");
        VoteOption optionNo = new VoteOption(topic, "No");
        VoteOption optionMaybe = new VoteOption(topic,"Maybe");
        topic.setVoteOptions(List.of(optionYes,optionNo,optionMaybe));

        poll = new Poll();
        poll.setTopic(topic);
        poll.setRoomCode("1984");

        topic.setPolls(List.of(poll));

        Vote vote = new Vote();
        vote.setVoteOption(optionYes);
        vote.setVoter(testUser);
        vote.setPoll(poll);

        // Save all the instances | Poll cascades to: Poll -> VoteOption(s) && Poll -> Instance(s)
        userDataRepository.save(testUser);
        topicRepository.save(topic);
        voteRepository.save(vote);
    }

    @Test
    public void testUserData() {
        assertNotNull(testUser.getUsername());
        assertNotNull(testUser.getEmail());
        assertNotNull(testUser.getPassword());

        assertTrue(userDataRepository.existsByUsername("John Testing"));

        UserData retrivedUserData = userDataRepository.getUserDataByUsernameAndPassword("John Testing", "123");
        assertNotNull(retrivedUserData.getId());
    }


    @Test
    public void testPoll(){
        assertEquals(topic.getName(), topicRepository.getTopicsByOwner(testUser).get(0).getName());
        assertEquals(poll.getTopic().getId() , pollRepository.getPollByRoomCode("1984").getTopic().getId());
    }

    @Test
    public void testVote(){
        assertEquals(1,voteRepository.countByPoll(poll));
        assertEquals(1,voteRepository.countByPollAndVoteOption(poll, poll.getTopic().getVoteOptions().get(0)));
        assertEquals(0,voteRepository.countByPollAndVoteOption(poll, poll.getTopic().getVoteOptions().get(1)));
    }
}
