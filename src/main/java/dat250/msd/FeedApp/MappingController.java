package dat250.msd.FeedApp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MappingController
{
    @GetMapping("/")
    public String index(){
        return "Hello World!";
    }

    @GetMapping("/error")
    public String error()
    {
        return "404";
    }
}

