package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

    Instance getInstanceByRoomCode(String roomCode);
}
