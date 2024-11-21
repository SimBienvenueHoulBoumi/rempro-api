package web.rempro_api.followed;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import web.rempro_api.users.Users;
import web.rempro_api.users.UsersRepository;
import web.rempro_api.utils.dto.FollowedRequest;
import web.rempro_api.utils.enums.LevelType;
import web.rempro_api.utils.exception.CustomAuthException;
import web.rempro_api.utils.exception.ExceptionAuthUtil;

/**
 * Service class for managing Followed entities.
 * Provides methods for creating, retrieving, updating, and deleting Followed
 * entities.
 */
@Service
@RequiredArgsConstructor
public class FollowedService {

    private final FollowedRepository followedRepository;
    private final UsersRepository usersRepository;
    private final ExceptionAuthUtil exceptionUtil;

    /**
     * Creates a new Followed entity based on the provided request and username.
     * 
     * @param request  - The data for the Followed entity.
     * @param username - The username of the user creating the Followed entity.
     * @return The created Followed entity.
     * @throws CustomAuthException if the user is not found or if the level type is
     *                             invalid.
     */
    @Transactional
    @Cacheable("followedCache")
    public Followed createFollowed(FollowedRequest request, String username) {
        Users user = getUserByUsername(username);
        return saveFollowed(request, user);
    }

    /**
     * Retrieves a Followed entity by its ID.
     * 
     * @param id - The ID of the Followed entity to retrieve.
     * @return The Followed entity with the specified ID.
     * @throws CustomAuthException if the Followed entity with the given ID does not
     *                             exist.
     */
    @Transactional(readOnly = true)
    @Cacheable("followedByIdCache")
    public Followed getFollowedById(Long id) {
        return followedRepository.findById(id)
                .orElseThrow(() -> exceptionUtil.createNotFoundException("Followed", id));
    }

    /**
     * Retrieves all Followed entities.
     * 
     * @return A list of all Followed entities.
     */
    @Transactional(readOnly = true)
    @Cacheable("allFollowedCache")
    public List<Followed> getAllFollowed() {
        return followedRepository.findAll();
    }

    /**
     * Retrieves all Followed entities created by a specific user.
     * 
     * @param username - The username of the user whose Followed entities are to be
     *                 retrieved.
     * @return A list of Followed entities created by the specified user.
     * @throws CustomAuthException if the user is not found.
     */
    @Transactional(readOnly = true)
    @Cacheable("followedByUserCache")
    public List<Followed> getAllFollowedByUser(String username) {
        Users user = getUserByUsername(username);
        return followedRepository.findByCreatedBy(user);
    }

    /**
     * Updates the details of an existing Followed entity based on the provided
     * request.
     * Only the fields provided in the request will be updated.
     * 
     * @param id      - The ID of the Followed entity to update.
     * @param request - The data to update the Followed entity with.
     * @return The updated Followed entity.
     * @throws CustomAuthException if the Followed entity with the given ID does not
     *                             exist.
     */
    @Transactional
    public Followed updateFollowed(Long id, FollowedRequest request) {
        Followed toUpdate = getFollowedById(id);

        // Validation des champs requis
        Optional.ofNullable(request.getName()).orElseThrow(() -> new CustomAuthException("Name is required.", 400));
        Optional.ofNullable(request.getLevelType())
                .orElseThrow(() -> new CustomAuthException("LevelType is required.", 400));
        Optional.ofNullable(request.getLevelNumber())
                .orElseThrow(() -> new CustomAuthException("LevelNumber is required.", 400));
        Optional.ofNullable(request.getEpisodeNumber())
                .orElseThrow(() -> new CustomAuthException("EpisodeNumber is required.", 400));

        // Mise à jour conditionnelle avec opérateur ternaire
        toUpdate.setName(request.getName() != null ? request.getName() : toUpdate.getName());
        toUpdate.setLevelType(
                request.getLevelType() != null ? parseLevelType(request.getLevelType()) : toUpdate.getLevelType());
        toUpdate.setLevelNumber(
                request.getLevelNumber() != null ? request.getLevelNumber() : toUpdate.getLevelNumber());
        toUpdate.setEpisodeNumber(
                request.getEpisodeNumber() != null ? request.getEpisodeNumber() : toUpdate.getEpisodeNumber());

        toUpdate.setUpdatedAt(Date.from(Instant.now()));

        return followedRepository.save(toUpdate);
    }

    /**
     * Deletes a Followed entity by its ID.
     * 
     * @param id - The ID of the Followed entity to delete.
     * @throws CustomAuthException if the Followed entity with the given ID does not
     *                             exist.
     */
    @Transactional
    public void deleteFollowed(Long id) {
        Followed followed = getFollowedById(id);
        followedRepository.delete(followed);
    }

    /**
     * Parses a string value representing a level type and converts it to a
     * LevelType enum.
     * 
     * @param levelTypeString - The string value representing the level type.
     * @return The corresponding LevelType enum.
     * @throws CustomAuthException if the level type string is invalid.
     */
    private LevelType parseLevelType(String levelTypeString) {
        try {
            return LevelType.valueOf(levelTypeString.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new CustomAuthException("Invalid level type: " + levelTypeString, 400);
        }
    }

    /**
     * Retrieves a user by their username.
     * 
     * @param username - The username of the user to retrieve.
     * @return The user with the specified username.
     * @throws CustomAuthException if the user with the given username does not
     *                             exist.
     */
    private Users getUserByUsername(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> exceptionUtil.createNotFoundException("User", username));
    }

    /**
     * Saves a Followed entity with the provided request data and user.
     * 
     * @param request - The data for the Followed entity.
     * @param user    - The user creating the Followed entity.
     * @return The saved Followed entity.
     */
    private Followed saveFollowed(FollowedRequest request, Users user) {
        var currentInstant = Instant.now();
        Followed followed = Followed.builder()
                .name(request.getName())
                .createdBy(user)
                .levelType(parseLevelType(request.getLevelType()))
                .levelNumber(request.getLevelNumber())
                .episodeNumber(request.getEpisodeNumber())
                .createdAt(Date.from(currentInstant))
                .updatedAt(Date.from(currentInstant))
                .build();
        return followedRepository.save(followed);
    }
}
