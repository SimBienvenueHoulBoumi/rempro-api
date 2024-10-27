package web.rempro_api.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import web.rempro_api.utils.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationProvider authProvider;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authRequest -> authRequest
						.requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
								"/webjars/**")
						.permitAll()
						.anyRequest()
						.authenticated())

				.sessionManagement(sessionManager -> sessionManager
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
