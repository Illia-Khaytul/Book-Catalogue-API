package io.github.khaytul.illia.book_catalogue_api.exception.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserNotAuthenticatedException extends AuthenticationException{

    public UserNotAuthenticatedException(String message) {
        super(message);
    }

    public UserNotAuthenticatedException(String message, Object... args) {
        super(String.format(message, args));
    }
    
}
