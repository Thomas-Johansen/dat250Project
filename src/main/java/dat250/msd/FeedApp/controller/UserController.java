package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.service.UserDataService;
import dat250.msd.FeedApp.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dat250.msd.FeedApp.service.FeedAppService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final FeedAppService feedAppService;
    private final UserDataService userDataService;
    private final VoteService voteService;

    public UserController(FeedAppService feedAppService, UserDataService userDataService, VoteService voteService) {
        this.feedAppService = feedAppService;
        this.userDataService = userDataService;
        this.voteService = voteService;
    }

    @GetMapping("/user")
    public ResponseEntity<UserData> getUser(@RequestHeader("Authorization") String sessionId) {
        UserData user = userDataService.getUserWithSessionId(sessionId);
        if (user == null) {
            return feedAppService.createMessageResponse("Invalid session credentials", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<UserData> createUser(@RequestBody UserData user) {
        if (feedAppService.getUserDataRepository().existsByUsername(user.getUsername())) {
            return feedAppService.createMessageResponse("Username taken.", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(userDataService.createUser(user), HttpStatus.OK);
    }

    /**
     * {
     *     "password": "123"    (hashed on client side)
     *     "email": "123@gmail.com"
     * }
     * */
    @PutMapping("/user")
    public ResponseEntity<UserData> updateUser(@RequestHeader("Authorization") String sessionId, @RequestBody UserData reqUser) {
        UserData user = userDataService.getUserWithSessionId(sessionId);
        if (user == null) {
            return feedAppService.createMessageResponse("Invalid session credentials", HttpStatus.NOT_FOUND);
        }

        if (user.getEmail() != null){
            userDataService.updateMail(user, reqUser.getEmail());
        }
        //TODO generate new sessionId and return ResponseDAO?
        if (user.getPassword() != null){
            userDataService.updatePassword(user, reqUser.getPassword());
        }

        //TODO refrain from sending back stored encrypted password?
        return new ResponseEntity<>(feedAppService.getUserDataRepository().save(user), HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<UserData> deleteUser(@RequestHeader("Authorization") String sessionId) {
        UserData user = userDataService.getUserWithSessionId(sessionId);
        if (user == null) {
            return feedAppService.createMessageResponse("Invalid session credentials", HttpStatus.NOT_FOUND);
        }

        //Delete evey vote from poll
        for (Topic topic : user.getTopics()) {
            for (Poll poll : topic.getPolls()) {
                voteService.removeVotes(poll);
            }
        }

        //Set every vote from user to null
        List<Vote> votes = feedAppService.getVoteRepository().getVotesByVoter(user);
        for (Vote vote : votes) {
            vote.setVoter(null);
        }

        feedAppService.getTopicRepository().deleteAll(user.getTopics());
        feedAppService.getUserDataRepository().delete(user);

        return feedAppService.createMessageResponse("User has been deleted.", HttpStatus.OK);
    }
}

