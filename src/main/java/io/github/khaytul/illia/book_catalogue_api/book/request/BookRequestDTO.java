package io.github.khaytul.illia.book_catalogue_api.book.request;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BookRequestDTO(

    @NotBlank(groups = Groups.Register.class)
    @Size(min = 1, max = 100)
    String title,

    @Size(max = 1000)
    String description,

    @NotBlank(groups = Groups.Register.class)
    @Size(min = 1, max = 50)
    String author,

    @PositiveOrZero
    Integer pages,

    @PastOrPresent
    Date releaseDate

) {

    public static interface Groups {
        interface Register{}
        interface Update{}
    }
    
}
