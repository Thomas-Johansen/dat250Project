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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    @ManyToOne
    @JsonBackReference(value = "user-topic")
    private UserData owner;

    @Setter
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    @JsonIdentityReference
    private List<Poll> polls;

    @Setter
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "topic-voteOptions")
    private List<VoteOption> voteOptions;
}
