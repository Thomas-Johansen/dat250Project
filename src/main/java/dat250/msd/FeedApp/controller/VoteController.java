package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoteController {
    private final FeedAppService feedAppService;

    public VoteController(FeedAppService feedAppService){
        this.feedAppService = feedAppService;
    }

    //TODO
    // Get votes for a poll
    //@GetMapping()

    //TODO
    // Create a vote for a poll
    //@PostMapping()

    //TODO
    // Delete a vote from a poll
    //@DeleteMapping()
}
