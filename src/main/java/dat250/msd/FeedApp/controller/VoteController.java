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

    @PostMapping("/vote")
    public Vote createVote(@RequestBody Vote vote){
        // If the requirement for a user to at most have one vote per instance
        // just add a check within this command.
        feedAppService.getVoteRepository().save(vote);
        return vote;
    }

    @PutMapping("/vote")
    public Vote updateVote(@RequestBody Vote vote, @RequestBody VoteOption option){
        vote.setVoteOption(option);
        feedAppService.getVoteRepository().save(vote);
        return vote;
    }
}
