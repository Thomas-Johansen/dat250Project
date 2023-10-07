package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.web.bind.annotation.*;

@RestController
public class PollController {
    private final FeedAppService feedAppService;

    public PollController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    /**
     * Get a poll of a topic using the roomCode or id
     * */
    @GetMapping("/poll")
    public Poll getPoll(@RequestParam(required = false) String roomCode, @RequestParam(required = false) Long id){
        if (id != null){
            return feedAppService.getPollRepository().getPollById(id);
        }
        if (roomCode != null){
            //TODO check if returned query is not null
            return feedAppService.getPollRepository().getPollByRoomCode(roomCode);

        }
        return new Poll();
    }

    /**
     * Create a new poll of a topic.
     * topicId of the Topic to connect the poll to.
     * The request body should contain:
     * {
     *     "roomCode":"1234",
     *     "startDate":"2020-01-12T12:00:00",
     *     "endDate":  "2023-12-24T12:00:00"
     * }
     * TODO require user auth (people can guess the topicId)
     * */
    @PostMapping("/poll")
    public Poll createPoll(@RequestParam Long id, @RequestBody Poll poll){
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);

        String roomCode = poll.getRoomCode();
        if (feedAppService.getPollRepository().getPollByRoomCode(roomCode) != null){
            //TODO return error
            System.out.println("Poll with identical roomCode already exists!");
            return new Poll();
        }
        // Connect poll to topic
        poll.setTopic(topic);

        // Add poll to topic
        topic.getPolls().add(poll);

        // When topic is saved polls are cascaded.
        feedAppService.getTopicRepository().save(topic);

        // Get the newly created poll
        return feedAppService.getPollRepository().getPollByRoomCode(roomCode);
    }

    /**
     * Update date of poll
     * {
     *     "startDate":"2020-01-12T12:00:00",
     *     "endDate":  "2023-12-24T12:00:00"
     * }
     * */
    @PutMapping("/poll")
    public Poll updatePoll(@RequestParam Long id, @RequestBody Poll updatePoll){
        Poll poll = feedAppService.getPollRepository().getPollById(id);

        poll.setStartDate(updatePoll.getStartDate());
        poll.setEndDate(updatePoll.getEndDate());
        feedAppService.getPollRepository().save(poll);

        return feedAppService.getPollRepository().getPollById(id);
    }

    @DeleteMapping("/poll")
    public Poll deletePoll(@RequestParam String roomCode){
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);

        feedAppService.removeVotes(poll);

        feedAppService.getPollRepository().delete(poll);
        return poll;
    }

}
