package dat250.msd.FeedApp.service;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.repository.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    public ResponseEntity<Vote> createVote(Vote vote) {
        Poll poll = getPollRepository().getPollByRoomCode(vote.getPoll().getRoomCode());
        UserData user = getUser(vote.getVoter().getUsername(), vote.getVoter().getPassword());
        VoteOption voteOption = getVoteOptionRepository().getVoteOptionById(vote.getVoteOption().getId());

        if (poll == null){
            return createMessageResponse("Vote Creation Failed: Poll with matching roomCode not found!", HttpStatus.NOT_FOUND);
        }
        if (user == null){
            return createMessageResponse("Vote Creation Failed: User not found", HttpStatus.NOT_FOUND);
        }
        if (voteOption == null){
            // Try to get vote options with label and topic
            if (vote.getVoteOption().getLabel() != null && poll.getTopic() != null){
                voteOption = getVoteOptionRepository().getVoteOptionByTopicAndLabel(poll.getTopic(),vote.getVoteOption().getLabel());
            }
            if (voteOption == null){
                return createMessageResponse("Vote Creation Failed: voteOption with id: "+vote.getVoteOption().getId()+" not found", HttpStatus.NOT_FOUND);
            }
        }

        // Check if already voted in poll
        if (getVoteRepository().existsByPollAndVoter(poll,user)){
            return createMessageResponse("Vote Creation Failed: User has already voted!",HttpStatus.CONFLICT);
        }
        // Check if poll is still open
        if (poll.getEndDate().isBefore(LocalDateTime.now())){
            return createMessageResponse("Vote Creation Failed: Poll is closed!",HttpStatus.CONFLICT);
        }
        vote.setPoll(poll);
        vote.setVoter(user);
        vote.setVoteOption(voteOption);
        return new ResponseEntity<>(voteRepository.save(vote),HttpStatus.OK);
    }

    public boolean isUserTopicOwner(String username, String pwd, Long id) {
        // Check that requester is owner of topic
        UserData userData = getUser(username,pwd);
        UserData owner = getTopicRepository().getTopicById(id).getOwner();
        return Objects.equals(userData, owner);
    }

    public <T> ResponseEntity<T> createMessageResponse(String message, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Message", message);
        return new ResponseEntity<>(headers, status);
    }
}