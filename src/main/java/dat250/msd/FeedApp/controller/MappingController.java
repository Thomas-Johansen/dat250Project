package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.UserData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import dat250.msd.FeedApp.service.FeedAppService;

import java.util.List;

@Controller
public class MappingController
{
    private final FeedAppService feedAppService;

    public MappingController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    @GetMapping("/users")
    @ResponseBody
    public List<UserData> getAllUsers() {
        return feedAppService.getAllUsers();
    }


    @GetMapping("/")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }


}

