package io.github.khaytul.illia.book_catalogue_api.book.request;

import java.time.LocalDate;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BookFiltering(
    
    @Size(min = 1, max = 100)
    String titleContains,

    @Size(min = 1, max = 50)
    String authorName,

    @PositiveOrZero
    Integer minPages,

    @PositiveOrZero
    Integer maxPages,

    LocalDate releasedBefore,

    @PastOrPresent
    LocalDate releasedAfter
    
) {

    public boolean isEmpty(){
        return titleContains == null && authorName == null && minPages == null && maxPages == null && releasedBefore == null && releasedAfter == null;
    }
    
}
