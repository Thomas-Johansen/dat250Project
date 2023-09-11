package dat250.msd.FeedApp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class FeedAppApplication {
	static final String PERSISTENCE_UNIT_NAME = "FeedApp";

	//@PersistenceContext(unitName = "FeedApp")
	//private EntityManager em;

	public static void main(String[] args) {

		try (
				EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
				EntityManager em = factory.createEntityManager()
			)
		{
			em.getTransaction().begin();
			/*
			UserData user = new UserData();
			user.setUsername("test");
			user.setPassword("123");
			user.setEmail("g@gmail.euro");
			user.setPolls(List.of());

			Poll poll = new Poll();
			poll.setName("Test Poll");
			poll.setOwner(user);

			Vote vote = new Vote();
			vote.setName("Test Vote");
			vote.setPoll(poll);

			poll.setVotes(List.of(vote));

			em.persist(user);
			em.persist(poll);
			em.persist(vote);

			*/
			em.getTransaction().commit();
		}
		SpringApplication.run(FeedAppApplication.class, args);
	}

}
