package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TopicController {
    private final FeedAppService feedAppService;

    public TopicController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    @GetMapping("/topic/{id}")
    public ResponseEntity<Topic> getTopic(@PathVariable Long id){
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(topic,HttpStatus.OK);
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
    public ResponseEntity<Topic> createTopic(@RequestParam String username, @RequestParam String pwd, @RequestBody Topic topic){
        if (topic.getName() == null || topic.getVoteOptions().isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserData owner = feedAppService.getUser(username,pwd);
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
     * */
    @PutMapping("/topic/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestParam String username, @RequestParam String pwd, @RequestBody List<VoteOption> voteOptions){
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null){
            return feedAppService.createMessageResponse("Topic not found with id: "+id, HttpStatus.NOT_FOUND);
        }
        if (!feedAppService.isUserTopicOwner(username, pwd, id)){
            return feedAppService.createMessageResponse("User is not owner of poll!", HttpStatus.UNAUTHORIZED);
        }

        // Add new voteOptions
        for (VoteOption voteOption : voteOptions){
            voteOption.setTopic(topic);
            topic.getVoteOptions().add(voteOption);
        }
        return new ResponseEntity<>(feedAppService.getTopicRepository().save(topic), HttpStatus.OK);
    }

    @DeleteMapping("/topic/{id}")
    public ResponseEntity<Topic> deleteTopic(@PathVariable Long id, @RequestParam String username, @RequestParam String pwd){
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null){
            return feedAppService.createMessageResponse("Topic not found with id: "+id, HttpStatus.NOT_FOUND);
        }
        if (!feedAppService.isUserTopicOwner(username, pwd, id)){
            return feedAppService.createMessageResponse("User is not owner of poll!", HttpStatus.UNAUTHORIZED);
        }

        // Remove all votes from every instance
        for (Poll poll : topic.getPolls()){
            feedAppService.removeVotes(poll);
        }
        feedAppService.getTopicRepository().delete(topic);
        return new ResponseEntity<>(topic,HttpStatus.OK);
    }


}
