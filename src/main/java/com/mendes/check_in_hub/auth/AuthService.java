package com.mendes.check_in_hub.auth;

import com.mendes.check_in_hub.auth.DTO.LoginRequest;
import com.mendes.check_in_hub.auth.DTO.LoginResponse;
import com.mendes.check_in_hub.security.JwtService;
import com.mendes.check_in_hub.user.DTO.UserResponse;
import com.mendes.check_in_hub.user.User;
import com.mendes.check_in_hub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login (LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                );

        authenticationManager.authenticate(authenticationToken);

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(token, UserResponse.fromEntity(user));
    }

}
