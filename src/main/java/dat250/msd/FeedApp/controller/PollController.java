package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PollController {
    private final FeedAppService feedAppService;

    public PollController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    /**
     * Get a poll of a topic using the roomCode
     * */
    @GetMapping("/poll")
    public ResponseEntity<Poll> getPollWithRoomCode(@RequestParam String roomCode){
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);
        if (poll == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(poll,HttpStatus.OK);
    }

    /**
     * Get a poll with the poll id
     * */
    @GetMapping("/poll/{id}")
    public ResponseEntity<Poll> getPollWithId(@PathVariable Long id){
        Poll poll = feedAppService.getPollRepository().getPollById(id);
        if (poll == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(poll,HttpStatus.OK);
    }

    /**
     * Create a new poll of a topic.
     * topicId of the Topic to connect the poll to.
     * poll/1?username=mark&pwd=123
     * The request body should contain:
     * {
     *     "roomCode":"1234",
     *     "startDate":"2020-01-12T12:00:00",
     *     "endDate":  "2023-12-24T12:00:00"
     * }
     * */
    @PostMapping("/poll/{topicId}")
    public ResponseEntity<Poll> createPoll(@PathVariable Long topicId,@RequestParam String username, @RequestParam String pwd, @RequestBody Poll poll){
        Topic topic = feedAppService.getTopicRepository().getTopicById(topicId);
        if (topic == null){
            return feedAppService.createMessageResponse("No topic with id: "+topicId, HttpStatus.NOT_FOUND);
        }
        if (!feedAppService.isUserTopicOwner(username,pwd,topicId)){
            return feedAppService.createMessageResponse("User is not owner of Topic.", HttpStatus.UNAUTHORIZED);
        }

        String roomCode = poll.getRoomCode();
        if (feedAppService.getPollRepository().getPollByRoomCode(roomCode) != null){
            return feedAppService.createMessageResponse("Poll with identical roomCode already exists!", HttpStatus.CONFLICT);
        }
        // Connect poll to topic
        poll.setTopic(topic);
        topic.getPolls().add(poll);

        // When topic is saved polls are cascaded.
        feedAppService.getTopicRepository().save(topic);

        // Get the newly created poll
        return new ResponseEntity<>(feedAppService.getPollRepository().getPollByRoomCode(roomCode),HttpStatus.CREATED);
    }

    /**
     * Update date of poll
     * {
     *     "startDate":"2020-01-12T12:00:00",
     *     "endDate":  "2023-12-24T12:00:00"
     * }
     * */
    @PutMapping("/poll/{id}")
    public ResponseEntity<Poll> updatePoll(@PathVariable Long id, @RequestBody Poll updatePoll){
        Poll poll = feedAppService.getPollRepository().getPollById(id);
        if (poll == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        poll.setStartDate(updatePoll.getStartDate());
        poll.setEndDate(updatePoll.getEndDate());

        feedAppService.getPollRepository().save(poll);

        return new ResponseEntity<>(feedAppService.getPollRepository().getPollById(id), HttpStatus.OK);
    }

    @DeleteMapping("/poll")
    public ResponseEntity<Poll> deletePoll(@RequestParam String roomCode){
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);
        if (poll == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        feedAppService.removeVotes(poll);
        feedAppService.getPollRepository().delete(poll);

        return new ResponseEntity<>(poll,HttpStatus.OK);
    }
}
