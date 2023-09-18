package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption,Long> {
}
