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
        PollRepository pollRepo = feedAppService.getPollRepository();
        VoteRepository voteRepo = feedAppService.getVoteRepository();
        InstanceRepository instanceRepo = feedAppService.getInstanceRepository();
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
        Poll poll = new Poll();
        poll.setOwner(user);
        poll.setName("TestPoll");

        Instance instance = new Instance();
        instance.setRoomCode("1234");
        instance.setStartDate(LocalDateTime.now());
        instance.setEndDate(LocalDateTime.of(2023, 12, 24,12,0));

        instance.setPoll(poll);

        VoteOption voteOption = new VoteOption();
        voteOption.setLabel("Toast");
        voteOption.setPoll(poll);

        Vote vote = new Vote();
        vote.setInstance(instance);
        vote.setVoteOption(voteOption);
        vote.setVoter(user);

        user.setPolls(List.of(poll));

        poll.setInstances(List.of(instance));
        poll.setVoteOptions(List.of(voteOption));

        // save users
        userRepo.save(user);
        userRepo.save(user2);

        pollRepo.save(poll);
        voteRepo.save(vote);
        //instanceRepo.save(instance);     //Jpa class is set to Cascade.ALL by Poll
        //voteOptionRepo.save(voteOption); //Jpa class is set to Cascade.ALL by Poll

        UserData retrieveUser = userRepo.getUserDataByUsernameAndPassword("Testuser1","123");
        System.out.println(retrieveUser.getUsername());

        Instance instance1 = instanceRepo.getInstanceByRoomCode("1234");
        System.out.println(instance1.getPoll().getName());

        return args -> {};
    }

}
