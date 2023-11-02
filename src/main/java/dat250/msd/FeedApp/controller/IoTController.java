package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.dto.ResponseDTO;
import dat250.msd.FeedApp.dto.VoteOptionDTO;
import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.service.FeedAppService;
import dat250.msd.FeedApp.service.UserDataService;
import dat250.msd.FeedApp.session.SessionRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IoTController {
    private final FeedAppService feedAppService;
    private final UserDataService userDataService;
    private final SessionRegistry sessionRegistry;

    public IoTController(FeedAppService feedAppService, UserDataService userDataService, SessionRegistry sessionRegistry) {
        this.feedAppService = feedAppService;
        this.userDataService = userDataService;
        this.sessionRegistry = sessionRegistry;
    }


    /**
     * Create a new Token for IoT
     * The token is connected to a specific poll.
     * Must be owner of Topic/Poll to generate token.
     */
    @PostMapping("/iot/{pollId}")
    public ResponseEntity<ResponseDTO> tokenWithId(@RequestHeader("Authorization") String sessionId,@PathVariable Long pollId) {
        UserData user = userDataService.getUserWithSessionId(sessionId);
        if (user == null){
            return feedAppService.createMessageResponse("Invalid session", HttpStatus.UNAUTHORIZED);
        }
        Poll poll = feedAppService.getPollRepository().getPollById(pollId);
        if (poll == null){
            return feedAppService.createMessageResponse("No poll with id: "+pollId, HttpStatus.NOT_FOUND);
        }

        if (!userDataService.isUserTopicOwner(sessionId,poll.getTopic())){
            return feedAppService.createMessageResponse("User is not owner of topic", HttpStatus.UNAUTHORIZED);
        }
        //TODO unregister previously created tokens

        // Create token
        final String ioTSessionId = sessionRegistry.registerIoTSession(poll.getId().toString());

        ResponseDTO response = new ResponseDTO();
        response.setSessionId(ioTSessionId);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    /**
     * Post with roomCode instead of poll-id.
     * */
    @PostMapping("/iot")
    public ResponseEntity<ResponseDTO> tokenWithRoomCode(@RequestHeader("Authorization") String sessionId,@RequestParam String roomCode){
        Poll poll = feedAppService.getPollRepository().getPollByRoomCode(roomCode);
        if (poll == null){
            return feedAppService.createMessageResponse("No poll with roomCode: "+roomCode,HttpStatus.NOT_FOUND);
        }
        return tokenWithId(sessionId,poll.getId());
    }

    /**
     * Update the connected poll with the number of votes each option received.
     * The corresponding label or voteOptionId should be valid voteOptions of Topic.
     * PUT /api/iot?token=ZGQ1YjM3ZGQtZDM2Ni00YmI5HEE2MTQtNDY0NjUxNDBjM2M4
     * body:
     * [
     *  {
     *      "lablel": "1st",
     *      "voteCount": 5
     *  },
     *  {
     *      "id": "2",
     *      "voteCount": 2
     *  }
     * ]
     * */
    @PutMapping("/iot")
    public ResponseEntity<String> putPoll(@RequestParam String token, @RequestBody List<VoteOptionDTO> voteOptionDTOList){
        String pollIdentifier = sessionRegistry.getPollIdForIoTSession(token);
        if (pollIdentifier == null){
            return feedAppService.createMessageResponse("Invalid IoT token", HttpStatus.NOT_FOUND);
        }
        Long pollId = Long.parseLong(pollIdentifier);
        Poll poll = feedAppService.getPollRepository().getPollById(pollId);
        if (poll == null){
            return feedAppService.createMessageResponse("No poll with id: "+pollId, HttpStatus.NOT_FOUND);
        }

        //Create votes
        for (VoteOptionDTO voteCountDTO : voteOptionDTOList){
            // for every time a voteOption was voted on
            for (int i = 0; i < voteCountDTO.getVoteCount(); i++){
                VoteOption selectedOption = feedAppService.getVoteOptionFromTopic(poll,voteCountDTO);

                if (selectedOption != null){
                    Vote vote = new Vote();
                    vote.setVoteOption(selectedOption);
                    vote.setPoll(poll);
                    vote.setVoter(null);

                    feedAppService.getVoteRepository().save(vote);
                }
            }
        }
        return feedAppService.createMessageResponse("Updated poll", HttpStatus.OK);
    }
}
