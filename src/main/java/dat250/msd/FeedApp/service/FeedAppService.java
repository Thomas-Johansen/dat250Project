package dat250.msd.FeedApp.service;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.repository.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
public class FeedAppService {
    private final UserDataRepository userDataRepository;
    private final TopicRepository topicRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;

    @Autowired
    public FeedAppService(UserDataRepository userDataRepository, TopicRepository topicRepository, VoteRepository voteRepository, PollRepository pollRepository, VoteOptionRepository voteOptionRepository)
    {
        this.userDataRepository = userDataRepository;
        this.topicRepository = topicRepository;
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
        this.voteOptionRepository = voteOptionRepository;
    }

    public UserData createUser(UserData user){
        userDataRepository.save(user);
        return user;
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
    public UserData updateMail(UserData user, String email){
        user.setEmail(email);
        userDataRepository.save(user);
        return user;
    }

    public void removeVotes(Poll poll) {
        //Remove votes from poll
        List<Vote> votes = voteRepository.getVotesByPoll(poll);
        voteRepository.deleteAll(votes);
    }
}