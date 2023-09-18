package dat250.msd.FeedApp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Entity
public class Instance {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    private Poll poll;

    @Setter
    private String roomCode;

    @Setter
    private Date startDate;

    @Setter
    private Date endDate;
}
