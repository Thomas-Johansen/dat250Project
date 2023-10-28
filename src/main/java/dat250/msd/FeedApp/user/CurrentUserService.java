package dat250.msd.FeedApp.user;

import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * used when authenticating users
 */
@Service
public class CurrentUserService implements UserDetailsService {
    private final FeedAppService feedAppService;

    @Autowired
    public CurrentUserService(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }
    @Override
    public UserData loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserData currentUser = feedAppService.getUserByUsername(username);
        if (currentUser == null) {
            throw new UsernameNotFoundException("Failed to find user with username" + username);
        }
        return currentUser;
    }
}
