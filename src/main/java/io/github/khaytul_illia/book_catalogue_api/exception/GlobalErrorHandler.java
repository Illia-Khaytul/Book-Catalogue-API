package io.github.khaytul_illia.book_catalogue_api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {

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

    @ExceptionHandler(UserNotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse userNotAuthenticatedHandler(UserNotAuthenticatedException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());

        return new ErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "User is not authenticated"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(
            error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        log.warn("Caught {}: {} - {}", e.getClass().getName(), e.getMessage(), errors);

        return new ErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid request parameters",
            errors
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerMethodValidationHandler(HandlerMethodValidationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getParameterValidationResults().forEach(
            error -> errors.putIfAbsent(
                error.getMethodParameter().getParameterName(),
                error.getResolvableErrors().getFirst().getDefaultMessage()
            )
        );

        log.warn("Caught {}: {} - {}", e.getClass().getName(), e.getMessage(), errors);

        return new ErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid request parameters",
            errors
        );
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse optimisticLockHandler(OptimisticLockingFailureException e){
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());

        return new ErrorResponse(
            HttpStatus.CONFLICT,
            "Concurrent modification error"
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMessageNotReadableHandler(HttpMessageNotReadableException e) {
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());

        return new ErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid request body"
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse noResourceFoundHandler(NoResourceFoundException e) {
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());

        return new ErrorResponse(
            HttpStatus.NOT_FOUND,
            "Resource not found"
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exceptionHandler(Exception e) {
        log.error("[EXCEPTION] An unexpected exception has occurred", e);

        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Something went wrong"
        );
    }

}
