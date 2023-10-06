package dat250.msd.FeedApp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class VoteOption {
    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @ManyToOne
    @JsonBackReference("poll-voteOptions")
    private Poll poll;

    @Setter
    private String label;

    public VoteOption(Poll poll, String label) {
        this.poll = poll;
        this.label = label;
    }

    public VoteOption() {

    }
}
