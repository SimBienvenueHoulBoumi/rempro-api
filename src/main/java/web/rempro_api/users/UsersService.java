package web.rempro_api.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.rempro_api.utils.exception.CustomAuthException;
import web.rempro_api.utils.jwt.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void updatePassword(String username, String currentPassword, String newPassword) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthException("User not found with username: " + username));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomAuthException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        usersRepository.save(user);
    }

    @Transactional
    public void deleteUserAccount(String username, String password) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthException("User not found with username: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomAuthException("Password is incorrect");
        }

        usersRepository.delete(user);
    }

    public Users getUserInfoFromToken(String token) {
        if (token == null || token.isEmpty() || !token.contains(".")) {
            throw new CustomAuthException("Invalid token format");
        }
    
        String username = jwtService.getUsernameFromToken(token);
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthException("User not found with username: " + username));
    }
    
}
