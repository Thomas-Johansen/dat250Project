package dat250.msd.FeedApp.dto;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.service.FeedAppService;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Poll DTO object for started and finished polls that should be published to dweet.io and stored in MongoDB.
 * */
@Getter
public class PollPublishDTO {
    private final String topicName;
    private final String roomCode;

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    private final VoteCountDTO voteCount;

    public PollPublishDTO(FeedAppService feedAppService, Poll poll) {
        this.topicName = poll.getTopic().getName();
        this.roomCode = poll.getRoomCode();
        this.startDate = poll.getStartDate();
        this.endDate = poll.getEndDate();
        this.voteCount = new VoteCountDTO(feedAppService,poll);
    }
}
