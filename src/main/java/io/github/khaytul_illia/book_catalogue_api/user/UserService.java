package io.github.khaytul_illia.book_catalogue_api.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void createUser() {

    }

    public void changePassword() {

    }

    public void deleteUser() {

    }

}
