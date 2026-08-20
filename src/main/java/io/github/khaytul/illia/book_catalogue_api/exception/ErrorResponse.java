package io.github.khaytul.illia.book_catalogue_api.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String message,
    Map<String, String> data
) {

    public ErrorResponse(HttpStatus status, String message){
        this(Instant.now(), status.value(), message, Map.of());
    }
    
    public ErrorResponse(HttpStatus status, String message, Map<String, String> data){
        this(Instant.now(), status.value(), message, data);
    }
    
}
