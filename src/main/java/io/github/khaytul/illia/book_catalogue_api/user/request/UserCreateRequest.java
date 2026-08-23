package io.github.khaytul.illia.book_catalogue_api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotNull
    @Size(min = 5, max = 50)
    String username,

    @NotNull
    @Size(min = 6, max = 50)
    String password
    
) {

}
