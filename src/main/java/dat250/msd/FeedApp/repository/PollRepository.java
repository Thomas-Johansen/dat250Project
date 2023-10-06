package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll,Long> {
    /**
     * Get every poll that is owned by a user.
     * @param owner User that has created/owns the poll
     * @return list of owned polls
     * */
    List<Poll> getPollsByOwner(UserData owner);

    Poll getPollById(Long id);
}
