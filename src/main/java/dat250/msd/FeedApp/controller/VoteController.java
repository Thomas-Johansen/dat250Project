package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Instance;
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
    public List<Vote> getVotes(@RequestParam(required = true) Instance instance){
        return feedAppService.getVoteRepository().getVotesByInstance(instance);
    }

    /**
     * Create a new vote
     * A vote object should contain at least:
     * {
     *     "voter":{
     *         "username": "Testuser1",
     *         "password": "123"
     *     },
     *     "instance": {
     *         "roomCode": "1234"
     *     }
     *     "voteOption":{
     *         "id": 1
     *     }
     * }
     * */
    @PostMapping("/vote")
    public Vote createVote(@RequestBody Vote vote) {
        Instance instance = feedAppService.getInstanceRepository().getInstanceByRoomCode(vote.getInstance().getRoomCode());
        UserData user = feedAppService.getUser(vote.getVoter().getUsername(), vote.getVoter().getPassword());
        VoteOption voteOption = feedAppService.getVoteOptionRepository().getVoteOptionById(vote.getVoteOption().getId());

        if (instance == null){
            System.out.println("Vote Creation Failed: Instance not found!");
            return new Vote();
        }
        if (user == null){
            System.out.println("Vote Creation Failed: User not found");
            return new Vote();
        }
        if (feedAppService.getVoteRepository().existsByInstanceAndVoter(instance,user)){
            System.out.println("Vote Creation Failed: User has already voted!");
            return new Vote();
        }
        vote.setInstance(instance);
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
