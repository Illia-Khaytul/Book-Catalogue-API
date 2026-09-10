package io.github.khaytul_illia.book_catalogue_api.exception;

public class DuplicateEntryException extends RuntimeException{

    public DuplicateEntryException(String message){
        super(message);
    }

    public DuplicateEntryException(String message, Object... args){
        super(String.format(message, args));
    }

}
