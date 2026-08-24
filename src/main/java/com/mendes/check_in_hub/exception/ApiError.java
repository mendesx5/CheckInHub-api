package com.mendes.check_in_hub.exception;

import java.time.LocalDateTime;

public record ApiError (
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {

}
