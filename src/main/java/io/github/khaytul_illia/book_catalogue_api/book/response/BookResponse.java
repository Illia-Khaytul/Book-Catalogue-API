package io.github.khaytul_illia.book_catalogue_api.book.response;

import io.github.khaytul_illia.book_catalogue_api.book.Book;

import java.time.LocalDate;

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
