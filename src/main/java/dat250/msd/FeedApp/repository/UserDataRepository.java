package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.UserData;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository extends JpaRepository<UserData,Long> {

    /**
     * Get UserData by username and password
     * @return UserData if exists.
     * */
    UserData getUserDataByUsernameAndPassword(String username, String password);

    /**
     * Check if given username is taken or not.
     * @return True if taken or False if available
     * */
    boolean existsByUsername(String username);

    UserData getUserDataById(Long id);
}
