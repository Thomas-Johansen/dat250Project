package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.service.FeedAppService;
import dat250.msd.FeedApp.service.UserDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VoteOptionController {

    private final FeedAppService feedAppService;
    private final UserDataService userDataService;

    public VoteOptionController(FeedAppService feedAppService, UserDataService userDataService) {
        this.feedAppService = feedAppService;
        this.userDataService = userDataService;
    }

    @GetMapping("/vote-option/{id}")
    public ResponseEntity<List<VoteOption>> getVoteOptions(@PathVariable Long id) {
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null) {
            return feedAppService.createMessageResponse("VoteOption Creation Failed: No topic provided!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(topic.getVoteOptions(), HttpStatus.OK);
    }

    @PostMapping("/vote-option/{topicId}")
    public ResponseEntity<VoteOption> createVoteOption(@RequestHeader(name = "Authorization") String sessionId, @PathVariable Long topicId, @RequestParam String label) {
        Topic topic = feedAppService.getTopicRepository().getTopicById(topicId);

        if (!userDataService.isUserTopicOwner(sessionId, topic)) {
            return feedAppService.createMessageResponse("User is not owner of topic with id: " + topicId, HttpStatus.UNAUTHORIZED);
        }
        return feedAppService.createVoteOption(topic, label);
    }

    @PutMapping("/vote-option/{id}")
    public ResponseEntity<VoteOption> updateVoteOption(@RequestHeader(name = "Authorization") String sessionId, @PathVariable Long id, @RequestParam String label) {
        VoteOption option = feedAppService.getVoteOptionRepository().getVoteOptionById(id);
        if (option == null) {
            return feedAppService.createMessageResponse("VoteOption not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        if (!userDataService.isUserTopicOwner(sessionId, option.getTopic())) {
            return feedAppService.createMessageResponse("User is not owner of topic with id: " + option.getTopic().getId(), HttpStatus.UNAUTHORIZED);
        }

        option.setLabel(label);
        feedAppService.getVoteOptionRepository().save(option);
        return new ResponseEntity<>(option, HttpStatus.OK);
    }

    @DeleteMapping("/vote-option/{id}")
    public ResponseEntity<VoteOption> deleteVoteOption(@RequestHeader(name = "Authorization") String sessionId, @PathVariable Long id) {
        VoteOption option = feedAppService.getVoteOptionRepository().getVoteOptionById(id);
        if (option == null) {
            return feedAppService.createMessageResponse("VoteOption not found with id: " + id, HttpStatus.NOT_FOUND);
        }

        if (!userDataService.isUserTopicOwner(sessionId, option.getTopic())) {
            return feedAppService.createMessageResponse("User is not owner of topic with id: " + option.getTopic().getId(), HttpStatus.UNAUTHORIZED);
        }

        feedAppService.getVoteOptionRepository().delete(option);
        return new ResponseEntity<>(option, HttpStatus.OK);
    }
}
