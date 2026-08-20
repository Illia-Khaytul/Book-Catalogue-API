package io.github.khaytul.illia.book_catalogue_api.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.InvalidPasswordException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.UserNotAuthenticatedException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DuplicateEntryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse duplicateEntryHandler(DuplicateEntryException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ErrorResponse(
            HttpStatus.CONFLICT, 
            e.getMessage()
        );
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundHandler(EntityNotFoundException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ErrorResponse(
            HttpStatus.NOT_FOUND, 
            e.getMessage()
        );
    }
    
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidPasswordHandler(InvalidPasswordException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST, 
            e.getMessage()
        );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidHandler(MethodArgumentNotValidException e){
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                error -> error.getField(), 
                error -> error.getDefaultMessage()
            ));

        log.warn("Caught {}: {} - {}", e.getClass().getName(), e.getMessage(), errors);
        
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST, 
            "Invalid payload parameters",
            errors
        );
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationHandler(ConstraintViolationException e){
        Map<String, String> errors = e.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                error -> {
                    String path = error.getPropertyPath().toString();
                    return path.substring(path.lastIndexOf('.') + 1);
                },
                error -> error.getMessage()
            ));

        log.warn("Caught {}: {} - {}", e.getClass().getName(), e.getMessage(), errors);
        
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST, 
            "Invalid request parameters",
            errors
        );
    }
    
    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse optimisticLockHandler(OptimisticLockException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ErrorResponse(
            HttpStatus.CONFLICT, 
            "Concurrent modification error"
        );
    }
    
    @ExceptionHandler(UserNotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse userNotAuthenticatedHandler(AuthenticationCredentialsNotFoundException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ErrorResponse(
            HttpStatus.UNAUTHORIZED, 
            "User is not authenticated"
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noResourceFoundHandler(NoResourceFoundException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ErrorResponse(
            HttpStatus.NOT_FOUND, 
            "Resource not found"
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exceptionHandler(Exception e){
        log.error("[EXCEPTION] An unexpected exception has occurred", e);
        
        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "Something went wrong"
        );
    }
    
}
