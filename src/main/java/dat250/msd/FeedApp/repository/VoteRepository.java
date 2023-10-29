package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.model.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {

    /**
     * Count the total number of votes on a topic poll
     * @param poll topic poll to count votes from
     * @return totalVotes
     * */
    int countByPoll(Poll poll);

    /**
     * Count the number of votes for a topic poll that matches the given voteOption
     * @param poll topic poll to count votes from
     * @param voteOption vote option to count votes from
     * @return totalVotes
     * */
    int countByPollAndVoteOption(Poll poll, VoteOption voteOption);

    boolean existsByPollAndVoter(Poll poll, UserData voter);

    /**
     * Get every vote a user has made
     * @param voter object of user
     * @return a list votes
     * */
    List<Vote> getVotesByVoter(UserData voter);

    List<Vote> getVotesByPoll(Poll poll);

    List<Vote> getVotesByVoteOption(VoteOption oldVoteOption);

    Vote getVoteByIdAndVoter(Long id, UserData user);
}
