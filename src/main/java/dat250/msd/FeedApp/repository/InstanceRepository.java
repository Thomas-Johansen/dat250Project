package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

    /**
     * Get the poll instance by using the roomCode
     * @param roomCode code for the instance
     * @return instance
     * */
    Instance getInstanceByRoomCode(String roomCode);

    /**
     * Get instance using id
     * The build in getReferenceById() does only a lazy fetch (only reference)
     * */
    Instance getInstanceById(Long instanceId);
}
