package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TopicController {
    private final FeedAppService feedAppService;

    public TopicController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    @GetMapping("/topic")
    public Topic getTopic(@RequestParam Long id){
        return feedAppService.getTopicRepository().getTopicById(id);
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
    public Topic createTopic(@RequestParam String username, @RequestParam String pwd, @RequestBody Topic topic){
        UserData owner = feedAppService.getUser(username,pwd);
        if (owner == null || topic.getName() == null){
            //TODO error
            return new Topic();
        }
        topic.setOwner(owner);

        return feedAppService.getTopicRepository().save(topic);
    }

    /**
     * Update the topic voteOptions using the topic id and a json body like:
     * PUT: topic?id=1
     * [
     *     {
     *         "label": "Frozen Toast"
     *     },
     *     {
     *         "label": "Toasted Toast"
     *     }
     * ]
     * */
    @PutMapping("/topic")
    public Topic updateTopic(@RequestParam Long id, @RequestBody List<VoteOption> voteOptions){
        //TODO require auth
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);

        for (VoteOption voteOption : voteOptions){
            voteOption.setTopic(topic);
            topic.getVoteOptions().add(voteOption);
        }
        feedAppService.getTopicRepository().save(topic);

        return feedAppService.getTopicRepository().getTopicById(id);
    }

    @DeleteMapping("/topic")
    public Topic deleteTopic(@RequestParam Long id){
        //TODO require auth
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        if (topic == null){
            System.out.println("No topic with id: "+id);
            return new Topic();
        }

        // Remove all votes from every instance
        for (Poll poll : topic.getPolls()){
            feedAppService.removeVotes(poll);
        }

        feedAppService.getTopicRepository().delete(topic);
        return topic;
    }
}
