package com.mendes.check_in_hub.checkin;

import com.mendes.check_in_hub.auth.DTO.LoginRequest;
import com.mendes.check_in_hub.checkin.DTO.CheckInRequest;
import com.mendes.check_in_hub.checkin.DTO.CheckInResponse;
import com.mendes.check_in_hub.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check-in")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    public ResponseEntity<CheckInResponse> createCheckIn (
            @Valid @RequestBody CheckInRequest request,
            Authentication authentication
    ) {
        User validator = (User) authentication.getPrincipal();

        CheckInResponse response = checkInService.createCheckIn(request, validator);

        return ResponseEntity.ok(response);
    }

}
