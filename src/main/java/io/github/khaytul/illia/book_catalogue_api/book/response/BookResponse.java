package io.github.khaytul.illia.book_catalogue_api.book.response;

import java.time.LocalDate;

import io.github.khaytul.illia.book_catalogue_api.book.Book;

public record BookResponse(
    Long id,
    String title,
    String description,
    String author,
    Integer pages,
    LocalDate releaseDate
) {

    public BookResponse(Book book){
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
