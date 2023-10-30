package dat250.msd.FeedApp.dto;

import dat250.msd.FeedApp.model.Poll;
import dat250.msd.FeedApp.model.VoteOption;
import dat250.msd.FeedApp.service.FeedAppService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Dao object for counting votes:
 * {
 *     "topicId": 1,
 *     "pollId": 1,
 *     "voteOptions": [
 *         {
 *             "id": 1,
 *             "label": "Yes",
 *             "voteCount": 1
 *         },
 *         {
 *             "id": 2,
 *             "label": "No",
 *             "voteCount": 0
 *         }
 *     ],
 *     "totalVotes": 1
 *     "private": false
 * }
 * */
@Getter
@Setter
public class VoteCountDTO {
    private final Long topicId;
    private final Long pollId;

    private final boolean isPrivate;

    private final List<VoteOptionDTO> voteOptions = new ArrayList<>();
    private int totalVotes = 0;

    public VoteCountDTO(FeedAppService feedAppService, Poll poll) {
        this.topicId = poll.getTopic().getId();
        this.pollId = poll.getId();
        this.isPrivate = poll.isPrivate();

        for (VoteOption voteOption : poll.getTopic().getVoteOptions()){
            int voteOptionCount = feedAppService.getVoteRepository().countByPollAndVoteOption(poll,voteOption);

            this.voteOptions.add(new VoteOptionDTO(voteOption,voteOptionCount));
            this.totalVotes += voteOptionCount;
        }
    }
}