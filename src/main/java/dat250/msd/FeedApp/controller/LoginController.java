package dat250.msd.FeedApp.controller;

import dat250.msd.FeedApp.dto.RegisterDTO;
import dat250.msd.FeedApp.dto.ResponseDTO;
import dat250.msd.FeedApp.dto.UserDTO;
import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.service.FeedAppService;
import dat250.msd.FeedApp.service.UserDataService;
import dat250.msd.FeedApp.service.VoteService;
import dat250.msd.FeedApp.session.SessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {
    @Autowired
    public AuthenticationManager manager;
    @Autowired
    public SessionRegistry sessionRegistry;
    @Autowired
    private FeedAppService feedAppService;
    @Autowired
    private UserDataService userDataService;

    @Autowired
    public BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody UserDTO user) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        final String sessionId = sessionRegistry.registerSession(user.getUsername());

        ResponseDTO response = new ResponseDTO();
        response.setSessionId(sessionId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String sessionId) {
        sessionRegistry.unregisterSession(sessionId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity verify(@RequestHeader("Authorization") String sessionId) {
        if(sessionRegistry.verifySession(sessionId)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> createUser(@RequestBody RegisterDTO user) {
        if (feedAppService.getUserDataRepository().existsByUsername(user.getUsername())) {
            return feedAppService.createMessageResponse("Username taken.", HttpStatus.CONFLICT);
        }
        UserData createdUser = new UserData();
        createdUser.setUsername(user.getUsername());
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createdUser.setEmail(user.getEmail());

        userDataService.createUser(createdUser);

        final String sessionId = sessionRegistry.registerSession(user.getUsername());

        ResponseDTO response = new ResponseDTO();
        response.setSessionId(sessionId);

        return ResponseEntity.ok(response);
    }
}
