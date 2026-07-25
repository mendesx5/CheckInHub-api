package com.mendes.check_in_hub.event.DTO;

import com.mendes.check_in_hub.event.Event;
import com.mendes.check_in_hub.event.EventStatus;
import com.mendes.check_in_hub.user.DTO.UserResponse;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDateTime dateTime,
        String location,
        int capacity,
        EventStatus status,
        UserResponse organizer
) {
    public static EventResponse fromEntity (Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDateTime(),
                event.getLocation(),
                event.getCapacity(),
                event.getStatus(),
                UserResponse.fromEntity(event.getOrganizer())
        );
    }
}
