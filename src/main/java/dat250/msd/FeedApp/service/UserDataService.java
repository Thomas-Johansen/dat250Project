package dat250.msd.FeedApp.service;

import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.repository.*;
import dat250.msd.FeedApp.session.SessionRegistry;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDataService {
    private final UserDataRepository userDataRepository;
    private final SessionRegistry sessionRegistry;
    private final PasswordEncoder passwordEncoder;

    public UserDataService(UserDataRepository userDataRepository, SessionRegistry sessionRegistry, @Lazy PasswordEncoder passwordEncoder)
    {
        this.userDataRepository = userDataRepository;

        this.sessionRegistry = sessionRegistry;
        this.passwordEncoder = passwordEncoder;
    }

    public UserData createUser(UserData user){
        userDataRepository.save(user);
        return user;
    }

    public UserData getUserWithSessionId(String sessionId) {
        String username = sessionRegistry.getUsernameForSession(sessionId);
        return userDataRepository.getUserDataByUsername(username);
    }

    public void updatePassword(UserData user, String new_pwd){
        // Hash new pwd
        user.setPassword(passwordEncoder.encode(new_pwd));
        userDataRepository.save(user);
    }

    public void updateMail(UserData user, String email){
        user.setEmail(email);
        userDataRepository.save(user);
    }

    /**
     * Check that requester is owner of topic.
     * @param sessionId Authentication header value of user.
     * @param topic Topic to check ownership of.
     * @return true if user is owner of topic.
     */
    public boolean isUserTopicOwner(String sessionId, Topic topic) {
        UserData userData = getUserWithSessionId(sessionId);
        UserData owner = topic.getOwner();
        return Objects.equals(userData, owner);
    }
}
