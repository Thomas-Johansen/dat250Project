package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.UserData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import dat250.msd.FeedApp.service.FeedAppService;

import java.util.List;

@RestController
public class MappingController
{
    private final FeedAppService feedAppService;

    public MappingController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    @GetMapping("/")
    public String index(){
        return "Hello World!";
    }

    @GetMapping("/users")
    public List<UserData> getAllUsers() {
        return feedAppService.getAllUsers();
    }


}

