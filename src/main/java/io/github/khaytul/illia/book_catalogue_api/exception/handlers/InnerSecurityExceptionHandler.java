package io.github.khaytul.illia.book_catalogue_api.exception.handlers;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.khaytul.illia.book_catalogue_api.exception.ExceptionResponseDTO;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.UserNotAuthenticatedException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class InnerSecurityExceptionHandler {

    @ExceptionHandler(UserNotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionResponseDTO userNotAuthenticatedHandler(AuthenticationCredentialsNotFoundException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ExceptionResponseDTO(
            HttpStatus.UNAUTHORIZED, 
            "User is not authenticated"
        );
    }  
    
}
