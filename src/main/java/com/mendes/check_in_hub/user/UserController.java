package com.mendes.check_in_hub.user;

import com.mendes.check_in_hub.user.DTO.UserRequest;
import com.mendes.check_in_hub.user.DTO.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create user", description = "Create a new user")
    @PostMapping
    public ResponseEntity<UserResponse> createUser (@RequestBody @Valid UserRequest userRequest) {
        UserResponse userResponse = userService.createUser(userRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponse.id())
                .toUri();
        return ResponseEntity.created(location).body(userResponse);
    }

}
