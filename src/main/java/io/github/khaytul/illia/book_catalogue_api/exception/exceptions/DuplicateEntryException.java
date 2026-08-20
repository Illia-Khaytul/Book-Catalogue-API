package io.github.khaytul.illia.book_catalogue_api.exception.exceptions;

public class DuplicateEntryException extends RuntimeException{

    public DuplicateEntryException(String message){
        super(message);
    }

    public DuplicateEntryException(String message, Object... args){
        super(String.format(message, args));
    }
    
}
