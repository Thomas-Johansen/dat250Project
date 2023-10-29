package dat250.msd.FeedApp.service;

import dat250.msd.FeedApp.model.*;
import dat250.msd.FeedApp.repository.*;
import dat250.msd.FeedApp.session.SessionRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Getter
@Service
public class FeedAppService {
    private final UserDataRepository userDataRepository;
    private final TopicRepository topicRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;

    private final SessionRegistry sessionRegistry;
    private final UserDataService userDataService;

    private final TaskScheduler taskScheduler;

    @Autowired
    public FeedAppService(UserDataRepository userDataRepository, TopicRepository topicRepository, VoteRepository voteRepository, PollRepository pollRepository, VoteOptionRepository voteOptionRepository, SessionRegistry sessionRegistry, UserDataService userDataService, TaskScheduler taskScheduler) {
        this.userDataRepository = userDataRepository;
        this.topicRepository = topicRepository;
        this.voteRepository = voteRepository;
        this.pollRepository = pollRepository;
        this.voteOptionRepository = voteOptionRepository;

        this.sessionRegistry = sessionRegistry;
        this.userDataService = userDataService;

        this.taskScheduler = taskScheduler;
    }

    public ResponseEntity<VoteOption> createVoteOption(Topic topic, String label) {
        if (topic == null) {
            return createMessageResponse("VoteOption Creation Failed: No topic provided!", HttpStatus.NOT_FOUND);
        }
        if (label == null) {
            return createMessageResponse("VoteOption Creation Failed: No label provided!", HttpStatus.NOT_FOUND);
        }
        VoteOption option = new VoteOption(topic, label);
        return new ResponseEntity<>(voteOptionRepository.save(option), HttpStatus.OK);
    }

    /**
     * If no voteOption id was sent, but voteOption label AND Topic voteOptions are present.
     */
    public VoteOption getVoteOptionFromTopic(Poll poll, VoteOption selectedVoteOption) {
        VoteOption voteOption = getVoteOptionRepository().getVoteOptionById(selectedVoteOption.getId());
        if (voteOption == null) {
            // Try to get vote options with label and topic
            if (selectedVoteOption.getLabel() != null && poll.getTopic() != null) {
                voteOption = getVoteOptionRepository().getVoteOptionByTopicAndLabel(poll.getTopic(), selectedVoteOption.getLabel());
            }
        }
        return voteOption;
    }

    /**
     * Schedule the publishing of poll info to Dweet.io when the poll starts
     * and final result to Dweet.io + Publish Message when poll ends.
     * */
    public void schedulePublish(Poll poll) {
        Long pollId = poll.getId();
        String zoneId = "Europe/Oslo";

        Analytics analytics = new Analytics(this);
        if (poll.getStartDate() != null) {
            taskScheduler.schedule(
                    () -> analytics.startPoll(pollId),
                    poll.getStartDate().atZone(ZoneId.of(zoneId)).toInstant()
            );
        }
        //Schedule completion for poll completion -> dweet + messaging publish
        if (poll.getEndDate() != null) {
            taskScheduler.schedule(
                    () -> analytics.endPoll(pollId),
                    poll.getEndDate().atZone(ZoneId.of(zoneId)).toInstant()
            );
        }
    }

    public <T> ResponseEntity<T> createMessageResponse(String message, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Message", message);
        return new ResponseEntity<>(headers, status);
    }
}