package com.mendes.check_in_hub.event;

import com.mendes.check_in_hub.event.DTO.EventRequest;
import com.mendes.check_in_hub.event.DTO.EventResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent (@RequestBody @Valid EventRequest eventRequest) {
        EventResponse eventResponse = eventService.createEvent(eventRequest);
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

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent (@PathVariable Long eventId) {
        eventService.cancelEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organizer-events/{eventId}")
    public ResponseEntity<List<EventResponse>> findByOrganizerId (@PathVariable Long eventId) {
        List<EventResponse> eventResponse = eventService.findOrganizerEvents(eventId);
        return ResponseEntity.ok(eventResponse);
    }

}
