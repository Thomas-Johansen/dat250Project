package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Topic;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.model.Vote;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dat250.msd.FeedApp.service.FeedAppService;

import java.util.List;

@RestController
public class UserController
{
    private final FeedAppService feedAppService;

    public UserController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    @PostMapping("/user")
    public ResponseEntity<UserData> createUser(@RequestBody UserData user){
        if (feedAppService.getUserDataRepository().existsByUsername(user.getUsername())){
            return feedAppService.createMessageResponse("Username taken.",HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(feedAppService.createUser(user),HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<UserData> getUser(@RequestParam String username, @RequestParam String pwd){
        UserData user = feedAppService.getUser(username, pwd);
        if (user == null){
            return feedAppService.createMessageResponse("Invalid user credentials",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
    @PutMapping("/user")
    public ResponseEntity<UserData> updateUser(@RequestBody UserData reqUser,@RequestParam String pwd,@RequestParam String email){
        UserData user = feedAppService.getUser(reqUser.getUsername(), reqUser.getPassword());
        if (user == null){
            return feedAppService.createMessageResponse("Invalid user credentials",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(feedAppService.updateMail(feedAppService.updatePassword(user, user.getPassword(), pwd), email),HttpStatus.OK);
    }

    @DeleteMapping("/user")
    public ResponseEntity<UserData> deleteUser(@RequestBody UserData reqUser){
        UserData user = feedAppService.getUser(reqUser.getUsername(), reqUser.getPassword());
        if (user == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Delete evey vote from poll
        for (Topic topic : user.getTopics()){
            for (Poll poll : topic.getPolls()){
                feedAppService.removeVotes(poll);
            }
        }

        //Set every vote from user to null
        List<Vote> votes = feedAppService.getVoteRepository().getVotesByVoter(user);
        for (Vote vote : votes){
            vote.setVoter(null);
        }

        feedAppService.getTopicRepository().deleteAll(user.getTopics());
        feedAppService.getUserDataRepository().delete(user);

        return feedAppService.createMessageResponse("User has been deleted.",HttpStatus.OK);
    }

}

