package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.UserData;
import org.springframework.web.bind.annotation.*;
import dat250.msd.FeedApp.service.FeedAppService;

@RestController
public class UserController
{
    private final FeedAppService feedAppService;

    public UserController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    @PostMapping("/user")
    public UserData createUser(@RequestBody UserData user){
        return feedAppService.createUser(user);
    }

    @GetMapping("/user")
    public UserData getUser(@RequestParam String username, @RequestParam String pwd){
        return feedAppService.getUser(username, pwd);
    }
    @PutMapping("/user")
    public UserData updateUser(@RequestBody UserData user,@RequestParam String pwd,@RequestParam String email){
        return feedAppService.updateMail(feedAppService.updatePassword(user.getId(), user.getPassword(), pwd), email);
    }

}

