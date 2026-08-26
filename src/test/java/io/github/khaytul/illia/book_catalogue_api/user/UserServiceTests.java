package io.github.khaytul.illia.book_catalogue_api.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.InvalidPasswordException;
import io.github.khaytul.illia.book_catalogue_api.security.AppUserDetails;
import io.github.khaytul.illia.book_catalogue_api.security.SecurityUtils;
import io.github.khaytul.illia.book_catalogue_api.user.request.PasswordChangeRequest;
import io.github.khaytul.illia.book_catalogue_api.user.request.UserCreateRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService tests")
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityUtils securityUtils;
    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("createUser tests")
    class CreateUserTests{

        private final UserCreateRequest request = new UserCreateRequest("username", "password");

        @Test
        @DisplayName("Should throw DuplicateEntryException when username is taken")
        void shouldThrowDuplicateEntryException_whenUsernameAlreadyExists() {
            //Arrange
            when(userRepository.existsByUsername(request.username()))
                .thenReturn(true);

            //Act and Assert
            assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateEntryException.class)
                .hasMessageContaining("User with provided username already exists");

            verify(userRepository).existsByUsername(request.username());
            verify(passwordEncoder, never()).encode(request.password());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should create and save user when username is not taken")
        void shouldEncodePasswordAndSave_whenUserDoesNotExist(){
            //Arrange
            when(userRepository.existsByUsername(request.username()))
                .thenReturn(false);
            when(passwordEncoder.encode(request.password()))
                .thenReturn(request.password());
            when(userRepository.save(any(User.class)))
                .thenReturn(new User());

            //Act
            userService.createUser(request);

            //Assert
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User user = userCaptor.getValue();
            assertThat(user.getId()).isNull();
            assertThat(user.getUsername()).isEqualTo(request.username());
            assertThat(user.getPassword()).isEqualTo(request.password());

            verify(userRepository).existsByUsername(request.username());
            verify(passwordEncoder).encode(request.password());
            verify(userRepository).save(any(User.class));
        }

    }
    
    @Nested
    @DisplayName("changePassword tests")
    class ChangePasswordTests{

        private final PasswordChangeRequest request = new PasswordChangeRequest("password", "newPassword");
        private final User authUser = new User(1L, "username", "password");

        @Test
        @DisplayName("Should throw InvalidPasswordException when new password is the same as old password")
        void shouldThrowInvalidPasswordException_whenNewPasswordMatchesOldPassword(){
            //Arrange
            PasswordChangeRequest request = new PasswordChangeRequest(this.request.oldPassword(), this.request.oldPassword());

            when(securityUtils.loadAuthenticatedUser())
                .thenReturn(authUser);

            //Act and Assert
            assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("New password cannot be the same as old password");
            
            verify(securityUtils).loadAuthenticatedUser();
            verify(passwordEncoder, never()).matches(request.oldPassword(), authUser.getPassword());
            verify(passwordEncoder, never()).encode(request.newPassword());
            verify(userRepository, never()).save(authUser);
        }

        @Test
        @DisplayName("Should throw InvalidPasswordException when old password does not match current password")
        void shouldThrowInvalidPasswordException_whenCurrentAndOldPasswordDoNotMatch(){
            //Arrange
            when(securityUtils.loadAuthenticatedUser())
                .thenReturn(authUser);
            when(passwordEncoder.matches(request.oldPassword(), authUser.getPassword()))
                .thenReturn(false);

            //Act and Assert
            assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Provided old password doesn't match current password");
            
            verify(securityUtils).loadAuthenticatedUser();
            verify(passwordEncoder).matches(request.oldPassword(), authUser.getPassword());
            verify(passwordEncoder, never()).encode(request.newPassword());
            verify(userRepository, never()).save(authUser);
        }

        @Test
        @DisplayName("Should change user password when new password is valid")
        void shouldChangeUserPasswordAndSave_whenNewPasswordIsValid(){
            //Arrange
            String authUserPassword = authUser.getPassword();

            when(securityUtils.loadAuthenticatedUser())
                .thenReturn(authUser);
            when(passwordEncoder.matches(request.oldPassword(), authUserPassword))
                .thenReturn(true);
            when(passwordEncoder.encode(request.newPassword()))
                .thenReturn(request.newPassword());
            when(userRepository.save(authUser))
                .thenReturn(new User());

            //Act
            userService.changePassword(request);
            
            //Assert
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User user = userCaptor.getValue();
            assertThat(user.getId()).isEqualTo(authUser.getId());
            assertThat(user.getUsername()).isEqualTo(authUser.getUsername());
            assertThat(user.getPassword()).isEqualTo(request.newPassword());

            verify(securityUtils).loadAuthenticatedUser();
            verify(passwordEncoder).matches(request.oldPassword(), authUserPassword);
            verify(passwordEncoder).encode(request.newPassword());
            verify(userRepository).save(authUser);
        }

    }
    
    @Nested
    @DisplayName("deleteUser tests")
    class DeleteUserTests{

        private final AppUserDetails userDetails = new AppUserDetails(new User(1L, "username", "password"));

        @Test
        @DisplayName("Should fetch authenticated user id and delete user")
        void shouldDeleteAuthenticatedUser(){
            //Arrange
            when(securityUtils.getAuthenticatedUserDetails())
                .thenReturn(userDetails);
            doNothing().when(userRepository)
                .deleteUserDirectly(userDetails.getUserId());

            //Act
            userService.deleteUser();

            //Assert
            verify(securityUtils).getAuthenticatedUserDetails();
            verify(userRepository).deleteUserDirectly(userDetails.getUserId());
        }

    }
    
}
