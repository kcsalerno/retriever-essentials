package re.api.controllers;

import re.api.domain.Result;
import re.api.domain.ResultType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

public class ErrorResponse {

    private final LocalDateTime timeStamp = LocalDateTime.now();
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public static <T> ResponseEntity<Object> build(Result<T> result) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (result.getType() == null || result.getType() == ResultType.INVALID || result.getType() == ResultType.DUPLICATE) {
            status = HttpStatus.BAD_REQUEST; // 400
        } else if (result.getType() == ResultType.NOT_FOUND) {
            status = HttpStatus.NOT_FOUND; // 404
        }

        return new ResponseEntity<>(result.getMessages(), status);
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }
}