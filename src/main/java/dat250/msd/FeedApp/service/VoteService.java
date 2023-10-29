package dat250.msd.FeedApp.service;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.repository.PollRepository;
import dat250.msd.FeedApp.repository.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final FeedAppService feedAppService;
    private final UserDataService userDataService;

    public VoteService(VoteRepository voteRepository, FeedAppService feedAppService, UserDataService userDataService, PollRepository pollRepository) {
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;

        this.feedAppService = feedAppService;
        this.userDataService = userDataService;
    }


    public void removeVotes(Poll poll) {
        //Remove votes from poll
        List<Vote> votes = voteRepository.getVotesByPoll(poll);
        voteRepository.deleteAll(votes);
    }

    public ResponseEntity<Vote> createVote(Vote vote) {
        Poll poll = pollRepository.getPollById(vote.getPoll().getId());
        if (poll == null){
            return feedAppService.createMessageResponse("Vote Creation Failed: Poll with matching roomCode not found!", HttpStatus.NOT_FOUND);
        }

        VoteOption selectedVoteOption = vote.getVoteOption();
        if (selectedVoteOption == null){
            return feedAppService.createMessageResponse("Vote Creation Failed: No voteOption provided!", HttpStatus.BAD_REQUEST);
        }

        // See if the Topic has the voteOption as a valid choice.
        VoteOption voteOption = feedAppService.getVoteOptionFromTopic(poll,selectedVoteOption);
        if (voteOption == null){
            return feedAppService.createMessageResponse("Vote Creation Failed: voteOption with id: "+vote.getVoteOption().getId()+" not found", HttpStatus.NOT_FOUND);
        }

        // Check if poll is still open
        if (poll.getEndDate().isBefore(LocalDateTime.now())){
            return feedAppService.createMessageResponse("Vote Creation Failed: Poll is closed!",HttpStatus.CONFLICT);
        }

        // TODO prevent users from voting multiple times on a public poll
        if (!poll.isPrivate()){
            vote.setPoll(poll);
            vote.setVoteOption(voteOption);
            vote.setVoter(null);
            return new ResponseEntity<>(voteRepository.save(vote),HttpStatus.OK);
        }
        return feedAppService.createMessageResponse("Vote Creation Failed: Tried to vote on a private poll.",HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<Vote> createPrivateVote(String sessionId, Vote vote){
        ResponseEntity<Vote> createPublicVote = createVote(vote);
        if (createPublicVote.getStatusCode() == HttpStatus.UNAUTHORIZED){
            UserData user = userDataService.getUserWithSessionId(sessionId);
            if (user == null){
                return feedAppService.createMessageResponse("Vote Creation Failed: User not found", HttpStatus.NOT_FOUND);
            }

            // Check if already voted in poll
            Poll poll = pollRepository.getPollById(vote.getPoll().getId());
            if (voteRepository.existsByPollAndVoter(poll,user)){
                return feedAppService.createMessageResponse("Vote Creation Failed: User has already voted!",HttpStatus.CONFLICT);
            }

            // Create vote
            vote.setPoll(poll);
            vote.setVoter(user);
            vote.setVoteOption(feedAppService.getVoteOptionFromTopic(poll,vote.getVoteOption()));

            return new ResponseEntity<>(voteRepository.save(vote),HttpStatus.OK);
        }
        return createPublicVote;
    }
}
