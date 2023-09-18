package dat250.msd.FeedApp.service;

import dat250.msd.FeedApp.model.UserData;
import dat250.msd.FeedApp.repository.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
public class FeedAppService {
    private final UserDataRepository userDataRepository;
    private final PollRepository pollRepository;
    private final InstanceRepository instanceRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;

    @Autowired
    public FeedAppService(UserDataRepository userDataRepository, PollRepository pollRepository, VoteRepository voteRepository, InstanceRepository instanceRepository, VoteOptionRepository voteOptionRepository)
    {
        this.userDataRepository = userDataRepository;
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.instanceRepository = instanceRepository;
        this.voteOptionRepository = voteOptionRepository;
    }

    public List<UserData> getAllUsers() {
        return userDataRepository.findAll();
    }

}