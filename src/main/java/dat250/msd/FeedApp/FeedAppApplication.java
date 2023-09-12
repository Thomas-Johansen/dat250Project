package dat250.msd.FeedApp;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.repository.PollRepository;
import dat250.msd.FeedApp.repository.UserDataRepository;
import dat250.msd.FeedApp.repository.VoteRepository;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
		poll.setRoomId("1");


		//Vote vote = new Vote();
		//vote.setName("TestVote");
		//vote.setPoll(poll);

		user.setPolls(List.of(poll));

		// save users
		userRepo.save(user);
		userRepo.save(user2);
		//pollRepo.save(poll);
		//voteRepo.save(vote);

		// fetch users
		for (UserData userData : userRepo.findAll()) {
			System.out.println(userData.toString());
		}

		return args -> {};
	}

}
