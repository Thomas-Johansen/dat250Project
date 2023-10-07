package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VoteController {
    private final FeedAppService feedAppService;

    public VoteController(FeedAppService feedAppService){
        this.feedAppService = feedAppService;
    }

    @GetMapping("/vote")
    public List<Vote> getVotes(@RequestParam(required = true) Poll poll){
        return feedAppService.getVoteRepository().getVotesByPoll(poll);
    }

    /**
     * Create a new vote
     * A vote object should contain at least:
     * {
     *     "voter":{
     *         "username": "Testuser1",
     *         "password": "123"
     *     },
     *     "poll": {
     *         "roomCode": "1234"
     *     }
     *     "voteOption":{
     *         "id": 1
     *     }
     * }
     * */
    @PostMapping("/vote")
    public Vote createVote(@RequestBody Vote vote) {
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(vote.getPoll().getRoomCode());
        UserData user = feedAppService.getUser(vote.getVoter().getUsername(), vote.getVoter().getPassword());
        VoteOption voteOption = feedAppService.getVoteOptionRepository().getVoteOptionById(vote.getVoteOption().getId());

        if (poll == null){
            System.out.println("Vote Creation Failed: Poll not found!");
            return new Vote();
        }
        if (user == null){
            System.out.println("Vote Creation Failed: User not found");
            return new Vote();
        }
        if (feedAppService.getVoteRepository().existsByPollAndVoter(poll,user)){
            System.out.println("Vote Creation Failed: User has already voted!");
            return new Vote();
        }
        vote.setPoll(poll);
        vote.setVoter(user);
        vote.setVoteOption(voteOption);

        return feedAppService.getVoteRepository().save(vote);
    }

    @PutMapping("/vote")
    public Vote updateVote(@RequestBody Vote vote, @RequestBody VoteOption option){
        vote.setVoteOption(option);
        feedAppService.getVoteRepository().save(vote);
        return vote;
    }
}
