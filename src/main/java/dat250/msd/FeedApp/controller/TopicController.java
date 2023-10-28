package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.service.FeedAppService;
import dat250.msd.FeedApp.session.SessionRegistry;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TopicController {
    private final FeedAppService feedAppService;
    @Autowired
    private final SessionRegistry sessionRegistry;

    public TopicController(FeedAppService feedAppService, SessionRegistry sessionRegistry) {
        this.feedAppService = feedAppService;
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("/topic/{id}")
    public ResponseEntity<Topic> getTopic(@PathVariable Long id){
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(topic,HttpStatus.OK);
    }

    @GetMapping("/topic")
    public ResponseEntity<List<Topic>> getTopics(@RequestParam String sessionId){
        String username = sessionRegistry.getUsernameForSession(sessionId);
        System.out.println("SESSION ID: "+sessionId);
        UserData user = feedAppService.getUser(username);
        List<Topic> topics = feedAppService.getTopicRepository().getTopicsByOwner(user);
        if (topics == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(topics,HttpStatus.OK);
    }

    /**
     * Create a new Topic. A topic should at least have a name and owner.
     * Vote options added with a PUT operation and Instances can be connected trough /instance POST:
     * Example of POST /topic?username=Testuser1&pwd=123
     * {
     *     "name": "Test Topic",
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
    @PostMapping("/topic")
    public ResponseEntity<Topic> createTopic(@RequestParam String sessionId, @RequestBody Topic topic){
        if (topic.getName() == null || topic.getVoteOptions().isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String username = sessionRegistry.getUsernameForSession(sessionId);
        UserData owner = feedAppService.getUser(username);
        if (owner == null){
            return feedAppService.createMessageResponse("User not found", HttpStatus.NOT_FOUND);
        }
        topic.setOwner(owner);
        return new ResponseEntity<>(feedAppService.getTopicRepository().save(topic), HttpStatus.CREATED);
    }

    /**
     * Add new voteOptions to the topic using the topic id+auth and a json body that looks like::
     * PUT: topic/id?username=mark&pwd=123
     * [
     *     {
     *         "label": "Frozen Toast"
     *     },
     *     {
     *         "label": "Toasted Toast"
     *     }
     * ]
     * You need to remove every voteOption reference in Vote as well.
     * */
    @PutMapping("/topic/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestParam String username, @RequestParam String pwd, @RequestBody List<VoteOption> voteOptions){
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null) {
            return feedAppService.createMessageResponse("Topic not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        if (!feedAppService.isUserTopicOwner(username, pwd, topic)) {
            return feedAppService.createMessageResponse("User is not the owner of the topic!", HttpStatus.UNAUTHORIZED);
        }
        // Remove all old VoteOptions and Votes with that option.
        for (VoteOption oldVoteOption : topic.getVoteOptions()){
            oldVoteOption.setTopic(null);
            feedAppService.getVoteRepository().deleteAll(feedAppService.getVoteRepository().getVotesByVoteOption(oldVoteOption));
        }

        // Add new voteOptions
        topic.getVoteOptions().clear();
        for (VoteOption voteOption : voteOptions){
            voteOption.setTopic(topic);
            topic.getVoteOptions().add(voteOption);
        }
        return new ResponseEntity<>(feedAppService.getTopicRepository().save(topic), HttpStatus.OK);
    }

    @DeleteMapping("/topic/{id}")
    public ResponseEntity<Topic> deleteTopic(@PathVariable Long id, @RequestParam String username, @RequestParam String pwd){
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null) {
            return feedAppService.createMessageResponse("Topic not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        if (!feedAppService.isUserTopicOwner(username, pwd, topic)) {
            return feedAppService.createMessageResponse("User is not the owner of the topic!", HttpStatus.UNAUTHORIZED);
        }

        // Remove all votes from every instance
        for (Poll poll : topic.getPolls()){
            feedAppService.removeVotes(poll);
        }
        feedAppService.getTopicRepository().delete(topic);
        return new ResponseEntity<>(topic,HttpStatus.OK);
    }
}
