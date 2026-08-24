package com.mendes.check_in_hub.exception;

public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException (Long id) {
        super("Enrollment not found with id: " + id);
    }

}
