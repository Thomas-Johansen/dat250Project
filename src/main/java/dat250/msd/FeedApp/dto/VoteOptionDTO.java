package dat250.msd.FeedApp.dto;

import dat250.msd.FeedApp.model.VoteOption;
import lombok.Getter;
@Getter
public class VoteOptionDTO {
    private final Long id;
    private final String label;
    private final int voteCount;

    public VoteOptionDTO(VoteOption voteOption, int count){
        this.id = voteOption.getId();
        this.label = voteOption.getLabel();
        this.voteCount = count;
    }
}