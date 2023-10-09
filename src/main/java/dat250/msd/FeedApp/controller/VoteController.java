package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VoteController {
    private final FeedAppService feedAppService;

    public VoteController(FeedAppService feedAppService){
        this.feedAppService = feedAppService;
    }

    @GetMapping("/vote")
    public ResponseEntity<List<Vote>> getVotes(@RequestParam String roomCode){
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);
        if (poll == null){
            return feedAppService.createMessageResponse("No poll with roomCode: "+roomCode, HttpStatus.NOT_FOUND);
        }

        List<Vote> votes = feedAppService.getVoteRepository().getVotesByPoll(poll);
        // Remove sensitive userData from output.
        for (Vote vote : votes){
            UserData user = vote.getVoter();
            sanitizeUserData(user);
        }
        return new ResponseEntity<>(votes,HttpStatus.OK);
    }

    /**
     * Create a new vote
     * A vote object should contain at least:
     * {
     *     "voter":{
     *         "username": "user1",
     *         "password": "123"
     *     },
     *     "poll": {
     *         "roomCode": "1234"
     *     }
     *     "voteOption":{
     *         "id": 1      |OR|    "label": "Toast"
     *     }
     * }
     * */
    @PostMapping("/vote")
    public ResponseEntity<Vote> createVote(@RequestBody Vote vote) {
        ResponseEntity<Vote> responseEntityVote = feedAppService.createVote(vote);
        if (responseEntityVote.getBody() == null){
            return responseEntityVote;
        }
        sanitizeUserData(responseEntityVote.getBody().getVoter());
        return responseEntityVote;
    }

    @PutMapping("/vote/{id}")
    public ResponseEntity<Vote> updateVote(@RequestBody Vote vote, @PathVariable Long id){
        vote.setVoteOption(feedAppService.getVoteOptionRepository().getVoteOptionById(id));
        feedAppService.getVoteRepository().save(vote);
        return new ResponseEntity<>(vote,HttpStatus.OK);
    }

    /**
     * Remove sensitive information from userData before sending.
     * */
    private void sanitizeUserData(UserData user){
        user.setPassword(null);
        user.setEmail(null);
        user.setTopics(null);
    }
}
