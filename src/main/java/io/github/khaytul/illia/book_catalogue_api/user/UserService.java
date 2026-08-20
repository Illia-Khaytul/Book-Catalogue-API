package io.github.khaytul.illia.book_catalogue_api.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.security.SecurityUtils;
import io.github.khaytul.illia.book_catalogue_api.user.request.UserPasswordChangeDTO;
import io.github.khaytul.illia.book_catalogue_api.user.request.UserRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void register(UserRequestDTO newUserData) {
        log.info("Registering new user with username '{}'", newUserData.username());

        log.debug("Checking if username is not taken");
        if(userRepository.existsByUsername(newUserData.username())){
            throw new DuplicateEntryException("User with username '%s' already exists", newUserData.username());
        }

        log.debug("Encoding password");
        String password = passwordEncoder.encode(newUserData.password());

        log.debug("Creating new user with provided data");
        User user = new User();
        user.setUsername(newUserData.username());
        user.setPassword(password);

        log.debug("Persisting new user");
        userRepository.save(user);

        log.info("New user successfully registered");
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void changePassword(UserPasswordChangeDTO passwordChangeData) {
        log.info("Changing current user's password");

        log.debug("Fetching authenticated user");
        String username = SecurityUtils.getAuthenticatedUserDetails().getUsername();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User with username '%s' does not exist", username));
        
        log.debug("Changind user password");
        user.setPassword(passwordEncoder.encode(passwordChangeData.newPassword()));

        log.debug("Persisting updated user");
        userRepository.save(user);

        log.info("User password successfully changed for user with username '{}'", username);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser() {
        log.info("Deleting current user");

        log.debug("Fetching authenticated user id");
        Long userId = SecurityUtils.getAuthenticatedUserDetails().getUserId();
        
        log.debug("Deleting user");
        userRepository.deleteUserDirectly(userId);

        log.info("Successfully deleted user with id {}", userId);
    }

}
