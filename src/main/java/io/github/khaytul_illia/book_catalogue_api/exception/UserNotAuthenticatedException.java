package io.github.khaytul_illia.book_catalogue_api.exception;

public class UserNotAuthenticatedException extends RuntimeException {

    public UserNotAuthenticatedException(String message) {
        super(message);
    }

}
