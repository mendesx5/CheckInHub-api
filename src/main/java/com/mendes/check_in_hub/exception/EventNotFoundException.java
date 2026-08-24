package com.mendes.check_in_hub.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException (Long id) {
        super("Event not found with id: " + id);
    }

}
