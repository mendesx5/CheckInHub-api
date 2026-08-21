package com.mendes.check_in_hub.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager (
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/events")
                        .hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.PUT, "/events/publish/**")
                        .hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.POST, "/check-in")
                        .hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.GET, "/enrollments/event/**")
                        .hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.GET, "/check-in/event/**"
                        ).hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.POST, "/enrollments")
                        .hasRole("PARTICIPANT")
                        .requestMatchers(HttpMethod.GET, "/enrollments/**")
                        .hasRole("PARTICIPANT")
                        .requestMatchers(HttpMethod.GET, "/enrollments/me")
                        .hasRole("PARTICIPANT")
                        .requestMatchers(HttpMethod.DELETE, "/enrollments/**")
                        .hasRole("PARTICIPANT")
                        .requestMatchers(HttpMethod.GET, "/enrollments/*/qrcode"
                        ).hasRole("PARTICIPANT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
