package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Instance;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.model.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {

    /**
     * Count the total number of votes on a poll instance
     * @param instance poll instance to count votes from
     * @return totalVotes
     * */
    int countByInstance(Instance instance);

    /**
     * Count the number of votes for a poll instance that matches the given voteOption
     * @param instance poll instance to count votes from
     * @param voteOption vote option to count votes from
     * @return totalVotes
     * */
    int countByInstanceAndVoteOption(Instance instance, VoteOption voteOption);

    /**
     * Get every vote a user has made
     * @param voter object of user
     * @return a list votes
     * */
    List<Vote> getVotesByVoter(UserData voter);
}
