package dat250.msd.FeedApp.service;

import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.repository.PollRepository;
import dat250.msd.FeedApp.repository.UserDataRepository;
import dat250.msd.FeedApp.repository.VoteRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
public class FeedAppService {
    private final UserDataRepository userDataRepository;
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public FeedAppService(UserDataRepository userDataRepository, PollRepository pollRepository, VoteRepository voteRepository) {
        this.userDataRepository = userDataRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
    }

    public List<UserData> getAllUsers() {
        return userDataRepository.findAll();
    }

}