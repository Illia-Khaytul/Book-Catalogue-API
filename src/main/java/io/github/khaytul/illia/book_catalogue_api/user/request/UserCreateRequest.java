package io.github.khaytul.illia.book_catalogue_api.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

    @NotBlank
    @Size(min = 5, max = 50)
    String username,

    @NotBlank
    @Size(min = 6, max = 50)
    String password
    
) {

}
