package web.rempro_api.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long>{
	Optional<Users> findByUsername(String username);
	List<Users> findByRole(Role role);
	boolean existsByUsername(String username);
}
