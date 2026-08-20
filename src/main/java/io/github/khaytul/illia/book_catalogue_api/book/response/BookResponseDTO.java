package io.github.khaytul.illia.book_catalogue_api.book.response;

import java.util.Date;

import io.github.khaytul.illia.book_catalogue_api.book.Book;

public record BookResponseDTO(
    Long id,
    String title,
    String description,
    String author,
    Integer pages,
    Date releaseDate
) {

    public BookResponseDTO(Book book){
        this(
            book.getId(), 
            book.getTitle(), 
            book.getDescription(), 
            book.getAuthor(), 
            book.getPages(), 
            book.getReleaseDate()
        );
    }
    
}
