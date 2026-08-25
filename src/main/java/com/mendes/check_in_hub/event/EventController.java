package com.mendes.check_in_hub.event;

import com.mendes.check_in_hub.event.DTO.EventRequest;
import com.mendes.check_in_hub.event.DTO.EventResponse;
import com.mendes.check_in_hub.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Events", description = "Event management endpoints")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create event", description = "Creates a new event in DRAFT status")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent (
            @RequestBody @Valid EventRequest eventRequest,
            Authentication authentication
    ) {
        User organizer = (User) authentication.getPrincipal();

        EventResponse eventResponse = eventService.createEvent(eventRequest, organizer);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(eventResponse.id())
                .toUri();
        return ResponseEntity.created(location).body(eventResponse);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> findByEventId (@PathVariable Long eventId) {
        EventResponse eventResponse = eventService.findByEventId(eventId);
        return ResponseEntity.ok(eventResponse);
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> findAllEvents () {
        List<EventResponse> eventResponse = eventService.findAllEvents();
        return ResponseEntity.ok(eventResponse);
    }

    @PutMapping("/publish/{eventId}")
    public ResponseEntity<Void> publishedEvent (
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        User organizer = (User) authentication.getPrincipal();
        eventService.publishEvent(eventId, organizer);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> cancelEvent (
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        User organizer = (User) authentication.getPrincipal();

        eventService.cancelEvent(eventId, organizer);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<EventResponse>> findMyEvents (Authentication authentication) {
        User organizer = (User) authentication.getPrincipal();

        return ResponseEntity.ok(eventService.findOrganizerEvents(organizer.getId()));
    }

}
