package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {

    /**
     * Get the topic poll by using the roomCode
     * @param roomCode code for the poll
     * @return poll
     * */
    Poll getPollByRoomCode(String roomCode);

    /**
     * Get poll using id
     * The built-in getReferenceById() only does a lazy fetch of the object.
     * */
    Poll getPollById(Long instanceId);
}
