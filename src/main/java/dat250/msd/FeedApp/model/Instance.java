package dat250.msd.FeedApp.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Instance {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @ManyToOne
    private Poll poll;

    @Setter
    @Column(unique = true)
    private String roomCode;

    @Setter
    private LocalDateTime startDate;

    @Setter
    private LocalDateTime endDate;
}
