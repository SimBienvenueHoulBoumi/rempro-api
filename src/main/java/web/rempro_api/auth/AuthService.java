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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Authentifie et génère un token JWT pour l'utilisateur.
     * 
     * @param request - les informations de connexion
     * @return AuthResponse - la réponse contenant le token
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginResquest request) {
        // Authentification via AuthenticationManager
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception ex) {
            throw new CustomAuthException("Invalid username or password");
        }

        // Récupérer l'utilisateur
        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomAuthException("User not found"));

        // Générer un token JWT
        String token = jwtService.getToken(user);

        return AuthResponse.builder().token(token).build();
    }

    /**
     * Enregistre un nouvel utilisateur dans la base de données.
     * 
     * @param request - les informations d'enregistrement
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomAuthException("Username already exists");
        }

        // Créer un nouvel utilisateur et encoder son mot de passe
        Users newUser = new Users();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Mot de passe encodé
        newUser.setRole(Role.USER);

        // Sauvegarder le nouvel utilisateur
        userRepository.save(newUser);

        // Charger les UserDetails pour générer un JWT
        UserDetails userDetails = userRepository.findByUsername(newUser.getUsername())
                .orElseThrow(() -> new CustomAuthException("User not found"));

        // Générer un token JWT pour le nouvel utilisateur
        String token = jwtService.getToken(userDetails);

        return AuthResponse.builder().token(token).build();
    }

    /**
     * Valide les informations d'enregistrement.
     * 
     * @param request - les informations d'enregistrement
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
