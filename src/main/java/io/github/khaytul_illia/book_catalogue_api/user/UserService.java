package io.github.khaytul_illia.book_catalogue_api.user;

import io.github.khaytul_illia.book_catalogue_api.exception.DuplicateEntryException;
import io.github.khaytul_illia.book_catalogue_api.exception.InvalidPasswordException;
import io.github.khaytul_illia.book_catalogue_api.security.SecurityUtils;
import io.github.khaytul_illia.book_catalogue_api.user.request.PasswordChangeRequest;
import io.github.khaytul_illia.book_catalogue_api.user.request.UserCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityUtils securityUtils){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
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

    public void changePassword(PasswordChangeRequest request) {
        log.info("Changing current user's password");

        log.debug("Fetching authenticated user");
        User user = securityUtils.loadAuthenticatedUser();

        log.debug("Checking if new password is different from old password");
        if(request.newPassword().equals(request.oldPassword())){
            throw new InvalidPasswordException("New password cannot be the same as old password");
        }

        log.debug("Checking if old passwords match");
        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())){
            throw new InvalidPasswordException("Provided old password doesn't match current password");
        }

        log.debug("Changing user password");
        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

        log.info("User password successfully changed for user with id {}", user.getId());
    }

    public void deleteUser() {

    }

}
