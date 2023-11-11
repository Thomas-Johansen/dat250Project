package dat250.msd.FeedApp.dto;

import lombok.Getter;
import lombok.Setter;

public class RegisterDTO {
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String email;
}
