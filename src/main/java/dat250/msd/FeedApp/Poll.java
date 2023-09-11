package dat250.msd.FeedApp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Date startDate;

    @Getter
    @Setter
    private Date endDate;

    @Getter
    @Setter
    private String roomId;

    @Getter
    @Setter
    @ManyToOne
    private UserData owner;

    @Getter
    @Setter
    @OneToMany(mappedBy = "poll")
    private List<Vote> votes;
}
