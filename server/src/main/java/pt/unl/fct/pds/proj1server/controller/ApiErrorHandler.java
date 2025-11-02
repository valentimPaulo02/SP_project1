package pt.unl.fct.pds.proj1server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex) {
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", ex.getReason(),
                            "status", 404,
                            "remaining", 0.0,
                            "timestamp", OffsetDateTime.now().toString()
                    ));
        }
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of(
                        "error", ex.getReason(),
                        "status", ex.getStatusCode().value(),
                        "timestamp", OffsetDateTime.now().toString()
                ));
    }
}
