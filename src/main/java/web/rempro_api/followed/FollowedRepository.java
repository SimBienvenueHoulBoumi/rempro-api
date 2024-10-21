package web.rempro_api.followed;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import web.rempro_api.utils.enums.LevelType;
import web.rempro_api.users.Users;


public interface FollowedRepository extends JpaRepository<Followed, Long> {
    List<Followed> findByLevelType(LevelType levelType);
    List<Followed> findByCreatedBy(Users createdBy);
}
