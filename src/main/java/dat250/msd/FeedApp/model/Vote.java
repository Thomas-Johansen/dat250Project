package dat250.msd.FeedApp.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
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
    @JsonIdentityReference(alwaysAsId = true)
    private Poll poll;

    @Setter
    @ManyToOne
    private VoteOption voteOption;

    @Setter
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private UserData voter;
}
