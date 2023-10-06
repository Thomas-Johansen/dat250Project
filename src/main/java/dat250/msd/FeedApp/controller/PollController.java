package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Instance;
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
     * Vote options added with a PUT operation and Instances can be connected trough /instance POST:
     * Example of POST /poll?username=Testuser1&pwd=123
     * {
     *     "name": "Test Poll",
     *     "voteOptions": [
     *         {
     *             "label": "Yes"
     *         },
     *         {
     *             "label": "Maybe"
     *         },
     *         {
     *             "label": "No"
     *         }
     *     ]
     * }
     * */
    @PostMapping("/poll")
    public Poll createPoll(@RequestParam String username, @RequestParam String pwd, @RequestBody Poll poll){
        UserData owner = feedAppService.getUser(username,pwd);
        if (owner == null || poll.getName() == null){
            //TODO error
            return new Poll();
        }
        poll.setOwner(owner);

        return feedAppService.getPollRepository().save(poll);
    }

    /**
     * Update the poll voteOptions using the poll id and a json body like:
     * PUT: poll?id=1
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
        if (poll == null){
            System.out.println("No poll with id: "+id);
            return new Poll();
        }

        // Remove all votes from every instance
        for (Instance instance : poll.getInstances()){
            feedAppService.removeVotes(instance);
        }

        feedAppService.getPollRepository().delete(poll);
        return poll;
    }
}
