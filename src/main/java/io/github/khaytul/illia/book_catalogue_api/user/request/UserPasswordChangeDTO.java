package io.github.khaytul.illia.book_catalogue_api.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordChangeDTO(
    
    @NotBlank
    @Size(min = 6, max = 50)
    String newPassword

) {

}
