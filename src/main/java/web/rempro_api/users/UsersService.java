package web.rempro_api.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.rempro_api.utils.exception.CustomAuthException;
import web.rempro_api.utils.exception.ExceptionAuthUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Service class that handles user-related operations such as updating passwords,
 * deleting user accounts, retrieving user information, and getting users by role.
 */
@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExceptionAuthUtil exceptionUtil;

    /**
     * Updates a user's password.
     *
     * @param username        The username of the user whose password is to be updated.
     * @param currentPassword The current password of the user.
     * @param newPassword     The new password to be set.
     * @throws CustomAuthException If the current password is incorrect or if the user does not exist.
     */
    @Transactional
    public void updatePassword(String username, String currentPassword, String newPassword) {
        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> exceptionUtil.createNotFoundException("User", username));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomAuthException("Current password is incorrect");
        }

        var encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        usersRepository.save(user);
    }

    /**
     * Deletes a user account.
     *
     * @param username The username of the user to be deleted.
     * @param password The password to validate the deletion.
     * @throws CustomAuthException If the password is incorrect or if the user does not exist.
     */
    @Transactional
    public void deleteUserAccount(String username, String password) {
        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> exceptionUtil.createNotFoundException("User", username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomAuthException("Password is incorrect");
        }

        usersRepository.delete(user);
    }

    /**
     * Retrieves user information by username.
     *
     * @param username The username of the user whose information is to be retrieved.
     * @return The user corresponding to the provided username.
     * @throws CustomAuthException If the user does not exist.
     */
    public Users getUserInfo(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> exceptionUtil.createNotFoundException("User", username));
    }

    /**
     * Retrieves all users with a specific role.
     *
     * @param role The role of the users to be retrieved (e.g., "ADMIN" or "USER").
     * @return A list of users with the specified role.
     * @throws CustomAuthException If the provided role is invalid.
     */
    public List<Users> getUsersByRole(String role) {
        Role roleEnum = switch (role.toUpperCase()) {
            case "ADMIN" -> Role.ADMIN;
            case "USER" -> Role.USER;
            default -> throw new CustomAuthException("""
                Invalid role: %s
                Please use a valid role and try again.""".formatted(role));
        };
        return usersRepository.findByRole(roleEnum);
    }
}
