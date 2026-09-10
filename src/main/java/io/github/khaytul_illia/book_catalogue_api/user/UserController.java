package io.github.khaytul_illia.book_catalogue_api.user;

import io.github.khaytul_illia.book_catalogue_api.user.request.UserCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(
        @Validated @RequestBody UserCreateRequest request
    ){
        userService.createUser(request);
    }

    @PatchMapping(path = "/password")
    public void changePassword(){

    }

    @DeleteMapping(path = "")
    public void deleteUser(){

    }

}
