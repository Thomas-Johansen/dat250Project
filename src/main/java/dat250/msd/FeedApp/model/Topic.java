package dat250.msd.FeedApp.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Json references:
 * User->Poll,
 * Instance->Poll
 * And:
 * Poll->VoteOptions
 * */
@Getter
@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    @ManyToOne
    @JsonBackReference(value = "user-poll")
    private UserData owner;

    @Setter
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Poll> polls;

    @Setter
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "poll-voteOptions")
    private List<VoteOption> voteOptions;
}
