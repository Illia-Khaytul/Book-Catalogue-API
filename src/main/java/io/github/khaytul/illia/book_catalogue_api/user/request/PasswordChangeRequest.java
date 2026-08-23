package io.github.khaytul.illia.book_catalogue_api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
    
    @NotNull
    @Size(min = 6, max = 50)
    String oldPassword,
    
    @NotNull
    @Size(min = 6, max = 50)
    String newPassword

) {

}
