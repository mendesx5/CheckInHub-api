package com.mendes.check_in_hub.user;

import com.mendes.check_in_hub.user.DTO.UserRequest;
import com.mendes.check_in_hub.user.DTO.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        UserRequest request = new UserRequest(
                "Gabriel",
                "gabriel@test.com",
                "123456",
                UserRole.PARTICIPANT
        );

        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Gabriel", response.name());
        assertEquals("gabriel@test.com", response.email());
        assertEquals(UserRole.PARTICIPANT, response.role());

        verify(passwordEncoder).encode("123456");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldSaveEncodedPassword() {
        UserRequest request = new UserRequest(
                "Gabriel",
                "gabriel@test.com",
                "123456",
                UserRole.PARTICIPANT
        );

        when(passwordEncoder.encode("123456")).thenReturn("$2a$encoded");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("$2a$encoded", savedUser.getPassword());

        assertNotEquals("123456", savedUser.getPassword());
    }
}
