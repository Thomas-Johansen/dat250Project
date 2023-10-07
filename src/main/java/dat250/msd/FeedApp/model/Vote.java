package dat250.msd.FeedApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    private Poll poll;

    @Setter
    @ManyToOne
    private VoteOption voteOption;

    @Setter
    @ManyToOne
    private UserData voter;
}
