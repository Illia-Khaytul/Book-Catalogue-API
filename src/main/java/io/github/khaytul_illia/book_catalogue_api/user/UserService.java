package io.github.khaytul_illia.book_catalogue_api.user;

import io.github.khaytul_illia.book_catalogue_api.exception.DuplicateEntryException;
import io.github.khaytul_illia.book_catalogue_api.user.request.UserCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserCreateRequest request) {
        log.info("Creating new user");

        log.debug("Checking if username is not taken");
        if(userRepository.existsByUsername(request.username())){
            throw new DuplicateEntryException("User with username '%s' already exists", request.username());
        }

        log.debug("Creating new user with provided data");
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        log.debug("Persisting new user");
        user = userRepository.save(user);

        log.info("New user successfully created with id {}", user.getId());
    }

    public void changePassword() {

    }

    public void deleteUser() {

    }

}
