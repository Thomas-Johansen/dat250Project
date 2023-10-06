package dat250.msd.FeedApp;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.repository.InstanceRepository;
import dat250.msd.FeedApp.repository.PollRepository;
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
    private PollRepository pollRepository;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private VoteRepository voteRepository;

    private UserData testUser;
    private Poll poll;
    private Instance instance;

    @BeforeAll
    public void setup(){
        testUser = new UserData();
        testUser.setUsername("John Testing");
        testUser.setPassword("123");
        testUser.setEmail("john@test.com");

        poll = new Poll();
        poll.setName("Test Poll");
        poll.setOwner(testUser);

        VoteOption optionYes = new VoteOption(poll, "Yes");
        VoteOption optionNo = new VoteOption(poll, "No");
        VoteOption optionMaybe = new VoteOption(poll,"Maybe");
        poll.setVoteOptions(List.of(optionYes,optionNo,optionMaybe));

        instance = new Instance();
        instance.setPoll(poll);
        instance.setRoomCode("1984");

        poll.setInstances(List.of(instance));

        Vote vote = new Vote();
        vote.setVoteOption(optionYes);
        vote.setVoter(testUser);
        vote.setInstance(instance);

        // Save all the instances | Poll cascades to: Poll -> VoteOption(s) && Poll -> Instance(s)
        userDataRepository.save(testUser);
        pollRepository.save(poll);
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
        assertEquals(poll.getName(), pollRepository.getPollsByOwner(testUser).get(0).getName());
        assertEquals(instance.getPoll().getId() ,instanceRepository.getInstanceByRoomCode("1984").getPoll().getId());
    }

    @Test
    public void testVote(){
        assertEquals(1,voteRepository.countByInstance(instance));
        assertEquals(1,voteRepository.countByInstanceAndVoteOption(instance, instance.getPoll().getVoteOptions().get(0)));
        assertEquals(0,voteRepository.countByInstanceAndVoteOption(instance, instance.getPoll().getVoteOptions().get(1)));
    }
}
