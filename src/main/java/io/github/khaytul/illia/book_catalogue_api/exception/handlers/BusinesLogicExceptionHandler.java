package io.github.khaytul.illia.book_catalogue_api.exception.handlers;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.khaytul.illia.book_catalogue_api.exception.ExceptionResponseDTO;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class BusinesLogicExceptionHandler {

    @ExceptionHandler(DuplicateEntryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponseDTO duplicateEntryHandler(DuplicateEntryException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ExceptionResponseDTO(
            HttpStatus.CONFLICT, 
            e.getMessage()
        );
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponseDTO entityNotFoundHandler(EntityNotFoundException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ExceptionResponseDTO(
            HttpStatus.NOT_FOUND, 
            e.getMessage()
        );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDTO methodArgumentNotValidHandler(MethodArgumentNotValidException e){
        Map<String, String> errors = e.getBindingResult().getAllErrors().stream()
            .collect(Collectors.toMap(
                error -> error.getObjectName(), 
                error -> error.getDefaultMessage()
            ));

        log.warn("Caught {}: {} - {}", e.getClass().getName(), e.getMessage(), errors);
        
        return new ExceptionResponseDTO(
            HttpStatus.BAD_REQUEST, 
            "Invalid payload parameters",
            errors
        );
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponseDTO constraintViolationHandler(ConstraintViolationException e){
        Map<String, String> errors = e.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                error -> {
                    String path = error.getPropertyPath().toString();
                    return path.substring(path.lastIndexOf('.') + 1);
                },
                error -> error.getMessage()
            ));

        log.warn("Caught {}: {} - {}", e.getClass().getName(), e.getMessage(), errors);
        
        return new ExceptionResponseDTO(
            HttpStatus.BAD_REQUEST, 
            "Invalid request parameters",
            errors
        );
    }
    
    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponseDTO optimisticLockHandler(OptimisticLockException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());
        
        return new ExceptionResponseDTO(
            HttpStatus.CONFLICT, 
            "Concurrent modification error"
        );
    }
    
}
