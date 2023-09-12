package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    //TODO Define custom query methods if needed
}
