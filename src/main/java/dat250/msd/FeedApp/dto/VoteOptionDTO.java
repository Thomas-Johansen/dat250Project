package dat250.msd.FeedApp.dto;

import dat250.msd.FeedApp.model.VoteOption;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteOptionDTO extends VoteOption{
    private Long id;
    private String label;
    private int voteCount;

    public VoteOptionDTO(VoteOption voteOption, int count){
        this.id = voteOption.getId();
        this.label = voteOption.getLabel();
        this.voteCount = count;
    }

    public VoteOptionDTO(){
    }
}