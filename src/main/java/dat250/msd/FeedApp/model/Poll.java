package dat250.msd.FeedApp.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Poll {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @ManyToOne
    @JsonIdentityReference
    private Topic topic;

    @Setter
    @Column(unique = true)
    private String roomCode;

    @Setter
    private LocalDateTime startDate;

    @Setter
    private LocalDateTime endDate;

    @Setter
    private boolean isPrivate = false;
}
