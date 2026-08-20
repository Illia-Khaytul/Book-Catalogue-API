package io.github.khaytul.illia.book_catalogue_api.book.request;

import java.time.LocalDate;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BookUpdateRequest(

    @Size(min = 1, max = 100)
    String title,

    @Size(max = 1000)
    String description,

    @Size(min = 1, max = 50)
    String author,

    @PositiveOrZero
    Integer pages,

    @PastOrPresent
    LocalDate releaseDate

) {

    public boolean isEmpty(){
        return (title == null || title.isBlank()) && (description == null || description.isBlank()) 
            && (author == null || author.isBlank()) && pages == null && releaseDate == null;
    }
    
}
