package web.rempro_api.followed;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import web.rempro_api.users.Users;
import web.rempro_api.users.UsersRepository;
import web.rempro_api.utils.dto.FollowedRequest;
import web.rempro_api.utils.enums.LevelType;
import web.rempro_api.utils.exception.CustomAuthException;

@Service
@RequiredArgsConstructor
public class FollowedService {

    private final FollowedRepository followedRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public Followed createFollowed(FollowedRequest request, String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthException("User not found with username: " + username));

        Followed followed = Followed.builder()
                .name(request.getName())
                .createdBy(user)
                .levelType(LevelType.valueOf(request.getLevelType().toUpperCase()))
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        return followedRepository.save(followed);
    }

    @Transactional(readOnly = true)
    public Optional<Followed> getFollowedById(Long id) {
        return followedRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Followed> getAllFollowed() {
        return followedRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Followed> getAllFollowedByUser(String username) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthException("User not found with username: " + username));
        return followedRepository.findByCreatedBy(user);
    }

    @Transactional
    public Followed updateFollowed(Long id, FollowedRequest request) {
        Followed toUpdate = followedRepository.findById(id)
                .orElseThrow(() -> new CustomAuthException("Followed not found with id: " + id));

        toUpdate.setName(request.getName());
        toUpdate.setLevelType(LevelType.valueOf(request.getLevelType().toUpperCase()));
        toUpdate.setUpdatedAt(new Date());

        return followedRepository.save(toUpdate);
    }

    @Transactional
    public void deleteFollowed(Long id) {
        if (!followedRepository.existsById(id)) {
            throw new CustomAuthException("Followed not found with id: " + id);
        }
        followedRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Followed> getFollowedByLevelType(String levelTypeString) {
        LevelType levelType;

        try {
            levelType = LevelType.valueOf(levelTypeString.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new CustomAuthException("Invalid level type: " + levelTypeString);
        }

        return followedRepository.findByLevelType(levelType);
    }
}
