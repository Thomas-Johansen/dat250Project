package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VoteOptionController {

    private final FeedAppService feedAppService;

    public VoteOptionController(FeedAppService feedAppService){
        this.feedAppService = feedAppService;
    }

    @GetMapping("/vote-option/{id}")
    public ResponseEntity<List<VoteOption>> getVoteOptions(@PathVariable Long id) {
        Topic topic = feedAppService.getTopicRepository().getTopicById(id);
        List<VoteOption> options = topic.getVoteOptions();

        return new ResponseEntity<>(options,HttpStatus.OK);
    }

    @PostMapping("/vote-option")
    public ResponseEntity<VoteOption> createVoteOption(@RequestBody Topic topic, @RequestParam String label) {
        ResponseEntity<VoteOption> responseEntityOption = feedAppService.createVoteOption(topic, label);

        return responseEntityOption;
    }

    @PutMapping("/vote-option")
    public ResponseEntity<VoteOption> updateVoteOption(@RequestBody VoteOption option, @RequestParam String label) {
        option.setLabel(label);
        feedAppService.getVoteOptionRepository().save(option);
        return new ResponseEntity<>(option,HttpStatus.OK);
    }

    @DeleteMapping("/vote-option")
    public ResponseEntity<VoteOption> deleteVoteOption(@RequestBody VoteOption option) {
        feedAppService.getVoteOptionRepository().delete(option);
        return new ResponseEntity<>(option,HttpStatus.OK);
    }
}
