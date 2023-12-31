package dat250.msd.FeedApp;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.repository.*;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication(scanBasePackages = "dat250.msd.FeedApp")
public class FeedAppApplication {
    final public BCryptPasswordEncoder passwordEncoder;

    public FeedAppApplication(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(FeedAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner createObjects(FeedAppService feedAppService) {
        // Get repos
        UserDataRepository userRepo = feedAppService.getUserDataRepository();
        TopicRepository topicRepo = feedAppService.getTopicRepository();
        VoteRepository voteRepo = feedAppService.getVoteRepository();

        UserData user = new UserData();
        user.setUsername("user1");
        user.setEmail("Test@email.com");
        user.setPassword(passwordEncoder.encode("123"));

        UserData user2 = new UserData();
        user2.setUsername("user2");
        user2.setEmail("Test2@email.no");
        user2.setPassword(passwordEncoder.encode("42"));

        //TODO make constructor / create service creation method
        Topic topic = new Topic();
        topic.setOwner(user);
        topic.setName("Test Topic 1");

        Topic topic2 = new Topic();
        topic2.setOwner(user);
        topic2.setName("Test Topic 2");

        Poll poll = new Poll();
        poll.setRoomCode("1234");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.of(2023, 12, 24,12,0));
        poll.setTopic(topic);

        Poll poll2 = new Poll();
        poll2.setRoomCode("4321");
        poll2.setTopic(topic);
        poll2.setStartDate(LocalDateTime.now());
        poll2.setEndDate(LocalDateTime.of(2023, 12, 24,12,0));
        poll2.setPrivate(true);

        VoteOption voteOption1 = new VoteOption(topic,"Yes");
        VoteOption voteOption2 = new VoteOption(topic,"No");

        user.setTopics(List.of(topic));

        topic.setPolls(List.of(poll,poll2));
        topic.setVoteOptions(List.of(voteOption1,voteOption2));

        topic2.setVoteOptions(List.of(new VoteOption(topic2, "1"),new VoteOption(topic2, "2"),new VoteOption(topic2, "3")));

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setVoteOption(voteOption1);
        vote.setVoter(user);

        // save users
        userRepo.save(user);
        userRepo.save(user2);

        //instanceRepo.save(instance);     //Jpa class is set to Cascade.ALL by Poll
        //voteOptionRepo.save(voteOption); //Jpa class is set to Cascade.ALL by Poll
        topicRepo.save(topic);
        topicRepo.save(topic2);

        voteRepo.save(vote);

        return args -> {};
    }

}
