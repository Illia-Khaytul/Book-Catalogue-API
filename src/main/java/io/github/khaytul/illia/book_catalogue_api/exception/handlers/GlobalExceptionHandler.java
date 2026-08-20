package io.github.khaytul.illia.book_catalogue_api.exception.handlers;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.github.khaytul.illia.book_catalogue_api.exception.ExceptionResponseDTO;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseDTO noResourceFoundHandler(NoResourceFoundException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ExceptionResponseDTO(
            HttpStatus.NOT_FOUND, 
            e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponseDTO exceptionHandler(Exception e){
        log.error("[EXCEPTION] An unexpected exception has occurred", e);
        
        return new ExceptionResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "Something went wrong"
        );
    }
    
}
