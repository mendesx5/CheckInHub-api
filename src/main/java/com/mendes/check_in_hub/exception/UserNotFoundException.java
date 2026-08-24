package com.mendes.check_in_hub.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException (Long id) {
        super("User not found with id " + id);
    }

}
