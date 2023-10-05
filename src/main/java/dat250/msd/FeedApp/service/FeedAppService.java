package dat250.msd.FeedApp.service;

import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.repository.*;
import lombok.Getter;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
public class FeedAppService {
    private final UserDataRepository userDataRepository;
    private final PollRepository pollRepository;
    private final InstanceRepository instanceRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;

    @Autowired
    public FeedAppService(UserDataRepository userDataRepository, PollRepository pollRepository, VoteRepository voteRepository, InstanceRepository instanceRepository, VoteOptionRepository voteOptionRepository)
    {
        this.userDataRepository = userDataRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.instanceRepository = instanceRepository;
        this.voteOptionRepository = voteOptionRepository;
    }

    public List<UserData> getAllUsers() {
        return userDataRepository.findAll();
    }
    public UserData getUser(String username, String pwd) {
        return userDataRepository.getUserDataByUsernameAndPassword(username, pwd);}
    public UserData updatePassword(Long user_id, String old_pwd, String new_pwd){
        UserData user = userDataRepository.getUserDataById(user_id);
        //TODO: old_pwd needs to be hashed to match the stored password.
        if(old_pwd.equals(user.getPassword())){
            //TODO: Hash new pwd
            user.setPassword(new_pwd);
            userDataRepository.save(user);
            return user;
        }
        throw new IllegalArgumentException();
    }
}