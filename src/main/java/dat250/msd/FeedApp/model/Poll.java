package dat250.msd.FeedApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Entity
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    @ManyToOne
    private UserData owner;

    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    private List<Instance> instance;

    @Setter
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    private List<VoteOption> voteOptions;
}
