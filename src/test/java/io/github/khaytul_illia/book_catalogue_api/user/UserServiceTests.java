package io.github.khaytul_illia.book_catalogue_api.user;

import io.github.khaytul_illia.book_catalogue_api.exception.DuplicateEntryException;
import io.github.khaytul_illia.book_catalogue_api.user.request.UserCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService tests")
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("createUser tests")
    class CreateUserTests {

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
                .hasMessageContaining(String.format("User with username '%s' already exists", request.username()));

            verify(userRepository).existsByUsername(request.username());
            verify(passwordEncoder, never()).encode(request.password());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should create and save user when username is not taken")
        void shouldEncodePasswordAndSave_whenUserDoesNotExist() {
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

}
