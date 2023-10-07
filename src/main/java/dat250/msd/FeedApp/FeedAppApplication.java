package dat250.msd.FeedApp;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.repository.*;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication(scanBasePackages = "dat250.msd.FeedApp")
public class FeedAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeedAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner createObjects(FeedAppService feedAppService) {
        // Get tables
        UserDataRepository userRepo = feedAppService.getUserDataRepository();
        TopicRepository pollRepo = feedAppService.getTopicRepository();
        VoteRepository voteRepo = feedAppService.getVoteRepository();
        PollRepository instanceRepo = feedAppService.getPollRepository();
        VoteOptionRepository voteOptionRepo = feedAppService.getVoteOptionRepository();


        UserData user = new UserData();
        user.setUsername("Testuser1");
        user.setEmail("Test@email.com");
        user.setPassword("123");

        UserData user2 = new UserData();
        user2.setUsername("Testuser2");
        user2.setEmail("Test2@email.no");
        user2.setPassword("42");

        //TODO make constructor / create service creation method
        Topic topic = new Topic();
        topic.setOwner(user);
        topic.setName("TestPoll");

        Poll poll = new Poll();
        poll.setRoomCode("1234");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.of(2023, 12, 24,12,0));

        poll.setTopic(topic);

        VoteOption voteOption = new VoteOption();
        voteOption.setLabel("Toast");
        voteOption.setTopic(topic);

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setVoteOption(voteOption);
        vote.setVoter(user);

        user.setTopics(List.of(topic));

        topic.setPolls(List.of(poll));
        topic.setVoteOptions(List.of(voteOption));

        // save users
        userRepo.save(user);
        userRepo.save(user2);

        pollRepo.save(topic);
        voteRepo.save(vote);
        //instanceRepo.save(instance);     //Jpa class is set to Cascade.ALL by Poll
        //voteOptionRepo.save(voteOption); //Jpa class is set to Cascade.ALL by Poll

        UserData retrieveUser = userRepo.getUserDataByUsernameAndPassword("Testuser1","123");
        System.out.println(retrieveUser.getUsername());

        Poll poll1 = instanceRepo.getPollByRoomCode("1234");
        System.out.println(poll1.getTopic().getName());

        return args -> {};
    }

}
