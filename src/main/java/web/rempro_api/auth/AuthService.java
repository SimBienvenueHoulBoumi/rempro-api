package web.rempro_api.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import web.rempro_api.users.Users;
import web.rempro_api.users.UsersRepository;
import web.rempro_api.utils.dto.AuthResponse;
import web.rempro_api.utils.dto.LoginResquest;
import web.rempro_api.utils.dto.RegisterRequest;
import web.rempro_api.utils.enums.Role;
import web.rempro_api.utils.exception.CustomAuthException;
import web.rempro_api.utils.jwt.JwtService;

/**
 * Service class responsible for handling user authentication and registration logic.
 * This class provides methods to authenticate users, generate JWT tokens, and register new users.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates the user and generates a JWT token upon successful login.
     *
     * @param request - The login request containing the username and password.
     * @return AuthResponse - The response containing the generated JWT token.
     * @throws CustomAuthException if the username or password is incorrect.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginResquest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception ex) {
            throw new CustomAuthException("Invalid username or password");
        }

        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomAuthException("User not found"));

        String token = jwtService.getToken(user);

        return AuthResponse.builder().token(token).build();
    }

    /**
     * Registers a new user and generates a JWT token for the new account.
     *
     * @param request - The register request containing the user details (username and password).
     * @return AuthResponse - The response containing the generated JWT token for the new user.
     * @throws CustomAuthException if the username already exists or if any validation fails.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomAuthException("Username already exists");
        }

        Users newUser = new Users();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Encoded password
        newUser.setRole(Role.USER);

        userRepository.save(newUser);

        UserDetails userDetails = userRepository.findByUsername(newUser.getUsername())
                .orElseThrow(() -> new CustomAuthException("User not found"));

        String token = jwtService.getToken(userDetails);

        return AuthResponse.builder().token(token).build();
    }

    /**
     * Validates the registration request to ensure that required fields are present and valid.
     *
     * @param request - The register request containing the user details.
     * @throws CustomAuthException if any validation fails.
     */
    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new CustomAuthException("Username cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new CustomAuthException("Password cannot be null or empty");
        }
    }
}
