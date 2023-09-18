package dat250.msd.FeedApp.repository;

import dat250.msd.FeedApp.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository extends JpaRepository<UserData,Long> {
    //TODO Define custom query methods if needed

    UserData getUserDataByUsernameAndPassword(String username, String password);
}
