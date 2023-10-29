package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.service.FeedAppService;
import dat250.msd.FeedApp.service.UserDataService;
import dat250.msd.FeedApp.service.VoteService;
import dat250.msd.FeedApp.session.SessionRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TopicController {
    private final FeedAppService feedAppService;
    private final UserDataService userDataService;
    private final SessionRegistry sessionRegistry;
    private final VoteService voteService;

    public TopicController(FeedAppService feedAppService, UserDataService userDataService, SessionRegistry sessionRegistry, VoteService voteService) {
        this.feedAppService = feedAppService;
        this.userDataService = userDataService;
        this.sessionRegistry = sessionRegistry;
        this.voteService = voteService;
    }

    @GetMapping("/topic/{id}")
    public ResponseEntity<Topic> getTopic(@PathVariable Long id) {
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    @GetMapping("/topic")
    public ResponseEntity<List<Topic>> getTopics(@RequestHeader("Authorization") String sessionId) {
        String username = sessionRegistry.getUsernameForSession(sessionId);
        System.out.println("SESSION ID: " + sessionId);

        UserData user = userDataService.getUserWithSessionId(username);

        List<Topic> topics = feedAppService.getTopicRepository().getTopicsByOwner(user);
        if (topics == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    /**
     * Create a new Topic. A topic should at least have a name and owner.
     * Vote options added with a PUT operation and Instances can be connected trough /instance POST:
     * Example of POST /topic
     * {
     * "name": "Test Topic",
     * "voteOptions": [
     * {
     * "label": "Yes"
     * },
     * {
     * "label": "Maybe"
     * },
     * {
     * "label": "No"
     * }
     * ]
     * }
     */
    @PostMapping("/topic")
    public ResponseEntity<Topic> createTopic(@RequestHeader("Authorization") String sessionId, @RequestBody Topic topic) {
        if (topic.getName() == null || topic.getVoteOptions().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserData owner = userDataService.getUserWithSessionId(sessionId);
        if (owner == null) {
            return feedAppService.createMessageResponse("User not found", HttpStatus.NOT_FOUND);
        }
        topic.setOwner(owner);
        return new ResponseEntity<>(feedAppService.getTopicRepository().save(topic), HttpStatus.CREATED);
    }

    /**
     * Replace all the voteOptions of a topic using the topic id+auth and a json body that looks like:
     * PUT: topic/id
     * [
     * {
     * "label": "Frozen Toast"
     * },
     * {
     * "label": "Toasted Toast"
     * }
     * ]
     * You need to remove every voteOption reference in Vote as well.
     */
    @PutMapping("/topic/{id}")
    public ResponseEntity<Topic> updateTopic(@RequestHeader("Authorization") String sessionId,
                                             @PathVariable Long id, @RequestBody List<VoteOption> voteOptions) {
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null) {
            return feedAppService.createMessageResponse("Topic not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        if (!userDataService.isUserTopicOwner(sessionId, topic)) {
            return feedAppService.createMessageResponse("User is not the owner of the topic!", HttpStatus.UNAUTHORIZED);
        }
        // Remove all old VoteOptions and Votes with that option.
        for (VoteOption oldVoteOption : topic.getVoteOptions()) {
            oldVoteOption.setTopic(null);
            feedAppService.getVoteRepository().deleteAll(feedAppService.getVoteRepository().getVotesByVoteOption(oldVoteOption));
        }

        // Add new voteOptions
        topic.getVoteOptions().clear();
        for (VoteOption voteOption : voteOptions) {
            voteOption.setTopic(topic);
            topic.getVoteOptions().add(voteOption);
        }
        return new ResponseEntity<>(feedAppService.getTopicRepository().save(topic), HttpStatus.OK);
    }

    @DeleteMapping("/topic/{id}")
    public ResponseEntity<Topic> deleteTopic(@RequestHeader("Authorization") String sessionId, @PathVariable Long id) {
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null) {
            return feedAppService.createMessageResponse("Topic not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        if (!userDataService.isUserTopicOwner(sessionId, topic)) {
            return feedAppService.createMessageResponse("User is not the owner of the topic!", HttpStatus.UNAUTHORIZED);
        }

        // Remove all votes from every instance
        for (Poll poll : topic.getPolls()) {
            voteService.removeVotes(poll);
        }
        feedAppService.getTopicRepository().delete(topic);
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }
}
