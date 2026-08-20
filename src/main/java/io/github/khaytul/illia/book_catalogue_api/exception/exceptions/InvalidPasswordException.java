package io.github.khaytul.illia.book_catalogue_api.exception.exceptions;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException(String message){
        super(message);
    }

    public InvalidPasswordException(String message, Object... args){
        super(String.format(message, args));
    }
    
}
