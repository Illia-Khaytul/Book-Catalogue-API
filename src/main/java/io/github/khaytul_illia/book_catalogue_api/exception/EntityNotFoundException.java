package io.github.khaytul_illia.book_catalogue_api.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }

}
