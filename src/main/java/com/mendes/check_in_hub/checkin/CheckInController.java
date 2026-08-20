package com.mendes.check_in_hub.checkin;

import com.mendes.check_in_hub.checkin.DTO.CheckInRequest;
import com.mendes.check_in_hub.checkin.DTO.CheckInResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/check-in")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    public ResponseEntity<CheckInResponse> createCheckIn (@Valid @RequestBody CheckInRequest checkInRequest) {
        CheckInResponse checkInResponse = checkInService.createCheckIn(checkInRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(checkInResponse.id())
                .toUri();
        return ResponseEntity.created(location).body(checkInResponse);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CheckInResponse>> findCheckInsByEvent (@PathVariable Long eventId) {
        List<CheckInResponse> checkInResponseList = checkInService.findCheckInsByEvent(eventId);
        return ResponseEntity.ok(checkInResponseList);
    }

}
