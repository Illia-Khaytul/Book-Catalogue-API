package io.github.khaytul.illia.book_catalogue_api.exception.exceptions;

public class EntityNotFoundException extends RuntimeException{

    public EntityNotFoundException(String message){
        super(message);
    }

    public EntityNotFoundException(String message, Object... args){
        super(String.format(message, args));
    }

}
