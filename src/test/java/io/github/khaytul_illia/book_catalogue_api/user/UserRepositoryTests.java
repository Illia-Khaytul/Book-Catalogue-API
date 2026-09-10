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

}
