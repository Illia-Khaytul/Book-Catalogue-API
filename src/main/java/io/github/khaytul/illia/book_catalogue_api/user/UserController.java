package io.github.khaytul.illia.book_catalogue_api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.khaytul.illia.book_catalogue_api.user.request.UserPasswordChangeDTO;
import io.github.khaytul.illia.book_catalogue_api.user.request.UserRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/users")
@Tag(name = "Users", description = "Endpoints to register, change password and delete users.")
@SecurityRequirement(name = "basicAuth")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user", description = """
        Creates a new user with a unique username and password.
        * Fails with '400 Bad Request' if the provided data is not valid.
        * Fails with '409 Conflict' if the provided username is already taken.
        """, security = {})
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Operation successful"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/400_response"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401_response"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/409_response"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/500_response")
    })
    public void register(
        @Valid @RequestBody UserRequestDTO newUserData
    ){
        userService.register(newUserData);
    }

    @PatchMapping(path = "/password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Changes the user's password", description = """
        Changes the password for the currently authenticated user.
        * Fails with '400 Bad Request' if the provided data is not valid.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operation successful"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/400_response"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401_response"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/500_response")
    })
    public void changePassword(
        @Valid @RequestBody UserPasswordChangeDTO passwordChangeData
    ){
        userService.changePassword(passwordChangeData);
    }

    @DeleteMapping(path = "")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes the user", description = """
        Deletes the currently authenticated user.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operation successful"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401_response"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/500_response")
    })
    public void deleteUser(){
        userService.deleteUser();
    }
    
}
