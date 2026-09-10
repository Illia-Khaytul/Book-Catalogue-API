package io.github.khaytul_illia.book_catalogue_api.user;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "")
    public void createUser(){

    }

    @PatchMapping(path = "/password")
    public void changePassword(){

    }

    @DeleteMapping(path = "")
    public void deleteUser(){

    }

}
