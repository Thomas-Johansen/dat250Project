package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteOptionRepository extends JpaRepository<VoteOption,Long> {

    VoteOption getVoteOptionById(Long id);

    VoteOption getVoteOptionByTopicAndLabel(Topic topic, String label);

    /**
     * Returns a list of all vote options belonging to a Topic
     * **/
    List<VoteOption> getVoteOptionsByTopic(Topic topic);
}
