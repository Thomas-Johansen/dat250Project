package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll,Long> {
    //TODO Define custom query methods if needed
    List<Poll> getPollsByOwner(UserData owner);

    //Poll getPollByRoomCode(String roomCode);
}
