package io.github.khaytul.illia.book_catalogue_api.book.request;

import java.util.Date;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BookFilterDTO(
    
    @Size(min = 1, max = 100)
    String titleContains,

    @Size(min = 1, max = 50)
    String authorName,

    @PositiveOrZero
    Integer minPages,

    @PositiveOrZero
    Integer maxPages,

    Date releasedBefore,

    @PastOrPresent
    Date releasedAfter
    
) {
    
}
