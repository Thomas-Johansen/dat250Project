package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll,Long> {
    //TODO Define custom query methods if needed
}
