package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PollController {
    private final FeedAppService feedAppService;

    public PollController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    @GetMapping("/poll")
    public Poll getPoll(@RequestParam Long id){
        return feedAppService.getPollRepository().getPollById(id);
    }

    /**
     * Create a new Poll. A poll should at least have a name and owner.
     * Vote options added with a PUT operation and Instances can be connected trough /instance POST.
     * */
    @PostMapping("/poll")
    public Poll createPoll(@RequestParam String username, @RequestParam String pwd, @RequestParam String pollName){
        UserData owner = feedAppService.getUser(username,pwd);

        Poll poll = new Poll();
        poll.setOwner(owner);
        poll.setName(pollName);

        return feedAppService.getPollRepository().save(poll);
    }

    /**
     * Update the poll voteOptions using the poll id and a json body like:
     * [
     *     {
     *         "label": "Frozen Toast"
     *     },
     *     {
     *         "label": "Toasted Toast"
     *     }
     * ]
     * */
    @PutMapping("/poll")
    public Poll updatePoll(@RequestParam Long id,@RequestBody List<VoteOption> voteOptions){
        //TODO require auth
        Poll poll = feedAppService.getPollRepository().getPollById(id);

        for (VoteOption voteOption : voteOptions){
            voteOption.setPoll(poll);
            poll.getVoteOptions().add(voteOption);
        }
        feedAppService.getPollRepository().save(poll);

        return feedAppService.getPollRepository().getPollById(id);
    }

    @DeleteMapping("/poll")
    public Poll deletePoll(@RequestParam Long id){
        //TODO require auth
        Poll poll = feedAppService.getPollRepository().getPollById(id);
        feedAppService.getPollRepository().delete(poll);
        return poll;
    }
}
