package com.mendes.check_in_hub.checkin;

import com.mendes.check_in_hub.checkin.DTO.CheckInRequest;
import com.mendes.check_in_hub.checkin.DTO.CheckInResponse;
import com.mendes.check_in_hub.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CheckIns", description = "CheckIn management endpoints")
@RestController
@RequestMapping("/check-in")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @Operation(summary = "Create check-in", description = "Create a new check-in")
    @PostMapping
    public ResponseEntity<CheckInResponse> createCheckIn (
            @Valid @RequestBody CheckInRequest request,
            Authentication authentication
    ) {
        User validator = (User) authentication.getPrincipal();

        CheckInResponse response = checkInService.createCheckIn(request, validator);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CheckInResponse>> findCheckInsByEvent (
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        User organizer = (User) authentication.getPrincipal();

        List<CheckInResponse> response = checkInService.findCheckInsByEvent(eventId, organizer);

        return ResponseEntity.ok(response);
    }

}
