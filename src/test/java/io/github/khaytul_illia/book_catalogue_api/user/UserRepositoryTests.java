package io.github.khaytul_illia.book_catalogue_api.user;

import io.github.khaytul_illia.book_catalogue_api.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("UserRepository tests")
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("findByUsername tests")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return user optional when user exists by username")
        void shouldReturnUser_whenUserExists() {
            //Arrange
            User user = new User();
            user.setUsername("username");
            user.setPassword("password");
            entityManager.persistAndFlush(user);

            //Act
            Optional<User> optional = userRepository.findByUsername(user.getUsername());

            //Assert
            assertThat(optional).isNotEmpty();
            assertThat(optional.get().getUsername()).isEqualTo(user.getUsername());
        }

        @Test
        @DisplayName("Should return empty optional when user does not exist by username")
        void shouldReturnEmpty_whenUserDoesNotExist() {
            //Act
            Optional<User> optional = userRepository.findByUsername("not exists");

            //Assert
            assertThat(optional).isEmpty();
        }

    }

    @Nested
    @DisplayName("existsByUsername tests")
    class ExistsByUsernameTests {

        @Test
        @DisplayName("Should return true when user exists by username")
        void shouldReturnTrue_whenUserExists() {
            //Arrange
            User user = new User();
            user.setUsername("username");
            user.setPassword("password");
            entityManager.persistAndFlush(user);

            //Act
            boolean exists = userRepository.existsByUsername(user.getUsername());

            //Assert
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when user does not exist by username")
        void shouldReturnFalse_whenUserDoesNotExist() {
            //Act
            boolean exists = userRepository.existsByUsername("not exists");

            //Assert
            assertThat(exists).isFalse();
        }

    }

    @Nested
    @DisplayName("deleteDirectlyById tests")
    class DeleteDirectlyByIdTests {

        @Test
        @DisplayName("Should delete the user by id when it exists")
        void shouldDeleteUser_whenUserExists() {
            //Arrange
            User user = new User();
            user.setUsername("username");
            user.setPassword("password");
            user = entityManager.persistAndFlush(user);
            entityManager.clear();

            //Act
            userRepository.deleteDirectlyById(user.getId());

            //Assert
            assertThat(entityManager.find(User.class, user.getId())).isNull();
        }

        @Test
        @DisplayName("Should do nothing if user does not exist")
        void shouldDoNothing_whenUserDoesNotExist() {
            //Arrange
            User user = new User();
            user.setUsername("username");
            user.setPassword("password");
            User newUser = entityManager.persistAndFlush(user);
            entityManager.remove(newUser);
            entityManager.flush();

            //Act and Assert
            assertThatCode(() -> userRepository.deleteDirectlyById(newUser.getId()))
                .doesNotThrowAnyException();

            assertThat(entityManager.find(User.class, user.getId())).isNull();
        }

    }

}
