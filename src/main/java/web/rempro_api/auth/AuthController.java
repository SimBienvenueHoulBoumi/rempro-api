package web.rempro_api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import web.rempro_api.utils.dto.AuthResponse;
import web.rempro_api.utils.dto.LoginResquest;
import web.rempro_api.utils.dto.RegisterRequest;

import org.springframework.http.HttpStatus;

@RequestMapping("/auth")
@Tag(name = "Auth", description = "API for managing authentications")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginResquest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
