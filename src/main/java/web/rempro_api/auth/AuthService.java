package web.rempro_api.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;

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
 * Service class responsible for handling user authentication and registration
 * logic. This class provides methods to authenticate users, generate JWT
 * tokens,
 * and register new users.
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
     * The token is then stored in a secure HttpOnly cookie.
     *
     * @param request  - The login request containing the username and password.
     * @param response - HttpServletResponse to set the token in a cookie.
     * @return AuthResponse - The response containing the generated JWT token.
     * @throws CustomAuthException if the username or password is incorrect.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginResquest request, HttpServletResponse response) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception ex) {
            throw new CustomAuthException("Invalid username or password");
        }

        // Retrieve the user details
        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomAuthException("User not found"));

        // Generate the JWT token
        String token = jwtService.getToken(user);

        // Store the token in a secure cookie
        response.addHeader("Set-Cookie",
                "token=" + token + "; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=3600");

        // Return the authentication response
        return AuthResponse.builder()
                .message("wellcome back")
                .build();
    }

    /**
     * Registers a new user and generates a JWT token for the new account.
     *
     * @param request - The register request containing the user details.
     * @return AuthResponse - The response containing the generated JWT token for
     *         the new user.
     * @throws CustomAuthException if the username already exists or if validation
     *                             fails.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        validateRegisterRequest(request);

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomAuthException("Username already exists");
        }

        // Create and save a new user
        Users newUser = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        Users savedUser = userRepository.save(newUser);

        // Generate the JWT token
        String token = jwtService.getToken(savedUser);

        // Store the token in a secure cookie
        response.addHeader("Set-Cookie", "token=" + token + "; Path=/; HttpOnly; Secure; SameSite=Strict");

        // Return the authentication response
        return AuthResponse.builder()
                .message("User registered successfully")
                .build();
    }

    /**
     * Validates the registration request to ensure required fields are present and
     * valid.
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

    /**
     * Logs out the user by invalidating the JWT token stored in the cookie.
     *
     * @param response - HttpServletResponse to clear the JWT cookie.
     */
    @Transactional
    public void logout(HttpServletResponse response) {
        // Invalidate the token by setting the cookie's Max-Age to 0
        response.setHeader("Set-Cookie", "token=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=Strict");
    }
}
