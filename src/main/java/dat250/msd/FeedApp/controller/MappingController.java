package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.UserData;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import dat250.msd.FeedApp.service.FeedAppService;

import java.util.List;

@RestController
public class MappingController
{
    private final FeedAppService feedAppService;

    public MappingController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    @PostMapping("/user")
    public UserData createUser(@RequestBody UserData user){
        return feedAppService.createUser(user);
    }
    @GetMapping("/users")
    @ResponseBody
    public List<UserData> getAllUsers() {
        return feedAppService.getAllUsers();
    }
    @GetMapping("/user")
    public UserData getUser(@RequestParam String username, @RequestParam String pwd){
        return feedAppService.getUser(username, pwd);
    }
    @PutMapping("/user")
    public UserData updateUser(@RequestAttribute UserData user, @RequestAttribute String username,@RequestAttribute String pwd,@RequestAttribute String email){
        return feedAppService.updatePassword(user.getId(), user.getPassword(), pwd);
    }
}

