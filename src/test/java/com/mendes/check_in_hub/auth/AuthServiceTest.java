package com.mendes.check_in_hub.auth;

import com.mendes.check_in_hub.auth.DTO.LoginRequest;
import com.mendes.check_in_hub.auth.DTO.LoginResponse;
import com.mendes.check_in_hub.security.JwtService;
import com.mendes.check_in_hub.user.User;
import com.mendes.check_in_hub.user.UserRepository;
import com.mendes.check_in_hub.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void validCredentialsShouldReturnToken() {
        LoginRequest request =
                new LoginRequest(
                        "user@test.com",
                        "123456"
                );

        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@test.com")
                .password("encoded")
                .role(UserRole.PARTICIPANT)
                .build();

        when(userRepository
                .findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        LoginResponse response =
                authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals(
                "user@test.com",
                response.user().email()
        );

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(jwtService).generateToken(user);
    }

    @Test
    void invalidCredentialsShouldFail() {
        LoginRequest request =
                new LoginRequest(
                        "user@test.com",
                        "wrong-password"
                );

        when(authenticationManager
                .authenticate(any()))
                .thenThrow(
                        new BadCredentialsException(
                                "Bad credentials"
                        )
                );

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(
                userRepository,
                jwtService
        );
    }

    @Test
    void authenticatedUserNotFoundShouldFail() {
        LoginRequest request =
                new LoginRequest(
                        "missing@test.com",
                        "123456"
                );

        when(userRepository
                .findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(any());
    }

}
