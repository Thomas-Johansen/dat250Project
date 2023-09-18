package dat250.msd.FeedApp.model;

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
    private Poll poll;

    @Setter
    private String label;
}
