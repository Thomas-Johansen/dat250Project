package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.model.Instance;
import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.Vote;
import dat250.msd.FeedApp.service.FeedAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InstanceController {
    private final FeedAppService feedAppService;

    public InstanceController(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }

    /**
     * Get an instance of a poll using the roomCode or id
     * */
    @GetMapping("/instance")
    public Instance getInstance(@RequestParam(required = false) String roomCode, @RequestParam(required = false) Long id){
        if (id != null){
            return feedAppService.getInstanceRepository().getInstanceById(id);
        }
        if (roomCode != null){
            //TODO check if returned query is not null
            return feedAppService.getInstanceRepository().getInstanceByRoomCode(roomCode);

        }
        return new Instance();
    }

    /**
     * Create a new instance of a poll.
     * pollId of the Poll to connect the instance to.
     * The request body should contain:
     * {
     *     "roomCode":"1234",
     *     "startDate":"2020-01-12T12:00:00",
     *     "endDate":  "2023-12-24T12:00:00"
     * }
     * TODO require user auth (people can guess the pollId)
     * */
    @PostMapping("/instance")
    public Instance createInstance(@RequestParam Long id, @RequestBody Instance instance){
        Poll poll = feedAppService.getPollRepository().getPollById(id);

        String roomCode = instance.getRoomCode();
        if (feedAppService.getInstanceRepository().getInstanceByRoomCode(roomCode) != null){
            //TODO return error
            System.out.println("Instance with identical roomCode already exists!");
            return new Instance();
        }
        // Connect instance to poll
        instance.setPoll(poll);

        // Add instance to poll
        poll.getInstances().add(instance);

        // When poll is saved instances are cascaded.
        feedAppService.getPollRepository().save(poll);

        // Get the newly created instance
        return feedAppService.getInstanceRepository().getInstanceByRoomCode(roomCode);
    }

    //@PutMapping("/instance")

    @DeleteMapping("/instance")
    public Instance deleteInstance(@RequestParam String roomCode){
        Instance instance = feedAppService.getInstanceRepository().getInstanceByRoomCode(roomCode);

        //Remove votes from instance
        List<Vote> votes = feedAppService.getVoteRepository().getVotesByInstance(instance);
        for (Vote vote : votes){
            feedAppService.getVoteRepository().delete(vote);
        }

        feedAppService.getInstanceRepository().delete(instance);
        return instance;
    }

}
