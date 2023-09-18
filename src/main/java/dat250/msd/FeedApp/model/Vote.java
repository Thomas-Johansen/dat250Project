package dat250.msd.FeedApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    private Instance instance;

    @Setter
    @ManyToOne
    private VoteOption voteOption;

    @Setter
    @ManyToOne
    private UserData voter;
}
