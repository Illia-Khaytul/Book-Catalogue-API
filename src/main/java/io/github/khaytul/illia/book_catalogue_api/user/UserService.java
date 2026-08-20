package io.github.khaytul.illia.book_catalogue_api.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.InvalidPasswordException;
import io.github.khaytul.illia.book_catalogue_api.security.SecurityUtils;
import io.github.khaytul.illia.book_catalogue_api.user.request.PasswordChangeRequest;
import io.github.khaytul.illia.book_catalogue_api.user.request.UserCreateRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void create(UserCreateRequest request) {
        log.info("Createing new user");

        log.debug("Checking if username is not taken");
        if(userRepository.existsByUsername(request.username())){
            throw new DuplicateEntryException("User with provided username already exists");
        }

        log.debug("Encoding password");
        String password = passwordEncoder.encode(request.password());

        log.debug("Creating new user with provided data");
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(password);

        log.debug("Persisting new user");
        user = userRepository.save(user);

        log.info("New user successfully createed with id {}", user.getId());
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        log.info("Changing current user's password");

        log.debug("Fetching authenticated user");
        String username = SecurityUtils.getAuthenticatedUserDetails().getUsername();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Authenticated user does not exist"));
        
        log.debug("Checking if new password is different from old password");
        if(!passwordEncoder.matches(request.newPassword(), user.getPassword())){
            throw new InvalidPasswordException("New password cannot be the same as old password");
        }

        log.debug("Changind user password");
        user.setPassword(passwordEncoder.encode(request.newPassword()));

        log.debug("Persisting updated user");
        userRepository.save(user);

        log.info("User password successfully changed for user with id {}", user.getId());
    }

    @Transactional
    public void deleteUser() {
        log.info("Deleting current user");

        log.debug("Fetching authenticated user id");
        Long userId = SecurityUtils.getAuthenticatedUserDetails().getUserId();
        
        log.debug("Deleting user");
        userRepository.deleteUserDirectly(userId);

        log.info("Successfully deleted user with id {}", userId);
    }

}
