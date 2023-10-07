package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic,Long> {
    /**
     * Get every topic that is owned by a user.
     * @param owner User that has created/owns the topic
     * @return list of owned topics
     * */
    List<Topic> getTopicsByOwner(UserData owner);

    Topic getTopicById(Long id);
}
