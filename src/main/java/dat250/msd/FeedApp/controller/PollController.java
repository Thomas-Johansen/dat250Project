package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.service.FeedAppService;
import dat250.msd.FeedApp.service.UserDataService;
import dat250.msd.FeedApp.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class PollController {
    private final FeedAppService feedAppService;
    private final VoteService voteService;
    private final UserDataService userDataService;

    private Random random = new Random();
    private HashSet<String> roomCodes = new HashSet();

    public PollController(FeedAppService feedAppService, VoteService voteService, UserDataService userDataService) {
        this.feedAppService = feedAppService;
        this.voteService = voteService;
        this.userDataService = userDataService;
    }

    /**
     * Get a poll of a topic using the roomCode
     */
    @GetMapping("/poll")
    public ResponseEntity<Poll> getPollWithRoomCode(@RequestParam String roomCode) {
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);
        if (poll == null) {
            return feedAppService.createMessageResponse("No poll with roomCode: " + roomCode, HttpStatus.NOT_FOUND);
        }
        // Hide other polls from output
        poll.getTopic().setPolls(null);
        return new ResponseEntity<>(poll, HttpStatus.OK);
    }

    /**
     * Get a poll with the poll id
     */
    @GetMapping("/poll/{id}")
    public ResponseEntity<Poll> getPollWithId(@PathVariable Long id) {
        Poll poll = feedAppService.getPollRepository().getPollById(id);
        if (poll == null) {
            return feedAppService.createMessageResponse("No poll with id: " + id, HttpStatus.NOT_FOUND);
        }
        poll.getTopic().setPolls(null);
        return new ResponseEntity<>(poll, HttpStatus.OK);
    }

    /**
     * Create a new poll of a topic.
     * topicId of the Topic to connect the poll to.
     * poll/1?username=mark&pwd=123
     * The request body should contain:
     * {
     * "roomCode":"1234",
     * "startDate":"2020-01-12T12:00:00",
     * "endDate":  "2023-12-24T12:00:00",
     * "isPrivate": true
     * }
     */
    @PostMapping("/poll/{topicId}")
    public ResponseEntity<Poll> createPoll(@RequestHeader("Authorization") String sessionId, @PathVariable Long topicId, @RequestBody Poll poll) {
        Topic topic = feedAppService.getTopicRepository().getTopicById(topicId);
        if (topic == null) {
            return feedAppService.createMessageResponse("No topic with id: " + topicId, HttpStatus.NOT_FOUND);
        }
        if (!userDataService.isUserTopicOwner(sessionId, topic)) {
            return feedAppService.createMessageResponse("User is not owner of Topic.", HttpStatus.UNAUTHORIZED);
        }
        //Generates a roomcode, which are stored in the hashmap serverside.
        poll.setRoomCode(createRoomCode());

        //The poll receives a different format of date, which we need to make into our format.
        LocalDateTime startDate = LocalDateTime.parse(poll.getStartDate().toString(), DateTimeFormatter.ISO_DATE_TIME);
        poll.setStartDate(startDate);

        LocalDateTime endDate = LocalDateTime.parse(poll.getEndDate().toString(), DateTimeFormatter.ISO_DATE_TIME);
        poll.setEndDate(endDate);

        String roomCode = poll.getRoomCode();
        if (feedAppService.getPollRepository().getPollByRoomCode(roomCode) != null) {
            return feedAppService.createMessageResponse("Poll with identical roomCode already exists!", HttpStatus.CONFLICT);
        }
        // Connect poll to topic
        poll.setTopic(topic);
        topic.getPolls().add(poll);

        // When topic is saved polls are cascaded.
        feedAppService.getTopicRepository().save(topic);
        poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);
        feedAppService.schedulePublish(poll);

        // Get the newly created poll
        return new ResponseEntity<>(poll, HttpStatus.CREATED);
    }

    /**
     * Update visibility of poll
     * {
     * "isPrivate": true
     * }
     */
    @PutMapping("/poll/{id}")
    public ResponseEntity<Poll> updatePoll(@RequestHeader("Authorization") String sessionId, @PathVariable Long id, @RequestBody Poll updatePoll) {
        Poll poll = feedAppService.getPollRepository().getPollById(id);
        if (poll == null) {
            return feedAppService.createMessageResponse("Poll not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        Topic topic = poll.getTopic();
        if (topic == null){
            return feedAppService.createMessageResponse("Poll has no topic!", HttpStatus.CONFLICT);
        }

        if (!userDataService.isUserTopicOwner(sessionId, poll.getTopic())) {
            return feedAppService.createMessageResponse("User is not the owner of the topic!", HttpStatus.UNAUTHORIZED);
        }
        poll.setPrivate(updatePoll.isPrivate());

        feedAppService.getPollRepository().save(poll);
        return new ResponseEntity<>(feedAppService.getPollRepository().getPollById(id), HttpStatus.OK);
    }

    /**
     * Delete a poll using id as path and userAuth as params
     * poll/{id}?username={x}&pwd={yz}
     */
    @DeleteMapping("/poll/{id}")
    public ResponseEntity<Poll> deletePoll(@RequestHeader("Authorization") String sessionId, @PathVariable Long id) {
        Poll poll = feedAppService.getPollRepository().getPollById(id);
        if (poll == null) {
            return feedAppService.createMessageResponse("Poll not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        if (!userDataService.isUserTopicOwner(sessionId, poll.getTopic())) {
            return feedAppService.createMessageResponse("User is not the owner of the topic!", HttpStatus.UNAUTHORIZED);
        }
        voteService.removeVotes(poll);
        feedAppService.getPollRepository().delete(poll);

        return new ResponseEntity<>(poll, HttpStatus.OK);
    }

    public String createRoomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++){
            code.append(random.nextInt(0, 9));
        }
        if(!roomCodes.contains(code.toString())){
            roomCodes.add(code.toString());
            return code.toString();
        }
        else {
            return createRoomCode();
        }
    }
}
