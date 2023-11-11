package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.dto.VoteCountDTO;
import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.service.FeedAppService;
import dat250.msd.FeedApp.service.UserDataService;
import dat250.msd.FeedApp.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class VoteController {
    private final FeedAppService feedAppService;
    private final UserDataService userDataService;
    private final VoteService voteService;

    public VoteController(FeedAppService feedAppService, UserDataService userDataService, VoteService voteService){
        this.feedAppService = feedAppService;
        this.userDataService = userDataService;
        this.voteService = voteService;
    }

    @GetMapping("/votes")
    public ResponseEntity<VoteCountDTO> getVoteCount(@RequestParam String roomCode){
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);
        if (poll == null){
            return feedAppService.createMessageResponse("No poll with roomCode: "+roomCode, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new VoteCountDTO(feedAppService,poll),HttpStatus.OK);
    }

    @GetMapping("/vote")
    public ResponseEntity<Vote> getVotes(@RequestHeader(name = "Authorization") String sessionId,@RequestParam String roomCode){
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);
        if (poll == null){
            return feedAppService.createMessageResponse("No poll with roomCode: "+roomCode, HttpStatus.NOT_FOUND);
        }
        if (!poll.isPrivate()){
            return feedAppService.createMessageResponse("Can not find specific vote by user on a Public Poll!", HttpStatus.CONFLICT);
        }

        UserData user = userDataService.getUserWithSessionId(sessionId);
        if (user == null){
            return feedAppService.createMessageResponse("Invalid sessionId.",HttpStatus.UNAUTHORIZED);
        }
        Vote vote = feedAppService.getVoteRepository().getVoteByPollAndVoter(poll,user);
        if (vote == null){
            return feedAppService.createMessageResponse("Could not find vote by user on poll",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vote,HttpStatus.OK);
    }

    /**
     * Create a new vote
     * A vote object should contain at least:
     * {
     *     "poll": {
     *         "id": 1      |OR|    "roomCode": "1234"
     *     }
     *     "voteOption":{
     *         "id": 1      |OR|    "label": "Toast"
     *     }
     * }
     * */
    @PostMapping("/vote")
    public ResponseEntity<Vote> createVote(@RequestHeader(name = "Authorization", required = false) String sessionId, @RequestBody Vote vote) {
        if (sessionId == null){
            return voteService.createVote(vote);
        }
        return voteService.createPrivateVote(sessionId,vote);
    }

    @PutMapping("/vote/{id}")
    public ResponseEntity<Vote> updateVote(@RequestHeader(name = "Authorization") String sessionId, @PathVariable Long id, @RequestBody VoteOption newVoteOption){
        UserData user = userDataService.getUserWithSessionId(sessionId);
        if (user == null){
            return feedAppService.createMessageResponse("Invalid sessionId.",HttpStatus.UNAUTHORIZED);
        }

        // See if user is owner of the vote
        Vote vote = feedAppService.getVoteRepository().getVoteByIdAndVoter(id,user);
        if (vote == null){
            return feedAppService.createMessageResponse("No vote with id: "+id+" belonging to user: "+user.getUsername(), HttpStatus.NOT_FOUND);
        }

        // See if given voteOption is a valid option in Topic
        VoteOption option = feedAppService.getVoteOptionFromTopic(vote.getPoll(),newVoteOption);
        if (option == null){
            return feedAppService.createMessageResponse("VoteOption is not a valid option in Topic", HttpStatus.CONFLICT);
        }
        vote.setVoteOption(option);
        feedAppService.getVoteRepository().save(vote);
        return new ResponseEntity<>(vote,HttpStatus.OK);
    }
}
