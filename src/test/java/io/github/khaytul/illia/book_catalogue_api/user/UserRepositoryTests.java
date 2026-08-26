package io.github.khaytul.illia.book_catalogue_api.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import io.github.khaytul.illia.book_catalogue_api.config.SliceTestcontainersConfig;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("UserRepository tests")
public class UserRepositoryTests extends SliceTestcontainersConfig{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("existsByUsername tests")
    class ExistsByUsernameTests{

        @Test
        @DisplayName("Should return true when user exists by username")
        void shouldReturnTrue_whenUserExists(){
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
        void shouldReturnFalse_whenUserDoesNotExist(){
            //Act
            boolean exists = userRepository.existsByUsername("not exists");

            //Assert
            assertThat(exists).isFalse();
        }
        
    }
    
    @Nested
    @DisplayName("findByUsername tests")
    class FindByUsernameTests{
        
        @Test
        @DisplayName("Should return user optional when user exists by username")
        void shouldReturnUser_whenUserExists(){
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
        void shouldReturnEmpty_whenUserDoesNotExist(){
            //Act
            Optional<User> optional = userRepository.findByUsername("not exists");

            //Assert
            assertThat(optional).isEmpty();
        }

    }
    
    @Nested
    @DisplayName("deleteUserDirectly tests")
    class DeleteUserDirectlyTests{
        
        @Test
        @DisplayName("Should delete user by id")
        void shouldDeleteUserById(){
            //Arrange
            User user = new User();
            user.setUsername("username");
            user.setPassword("password");
            user = entityManager.persistAndFlush(user);

            //Act
            userRepository.deleteUserDirectly(user.getId());
            entityManager.clear();

            //Assert
            assertThat(entityManager.find(User.class, user.getId())).isNull();
        }
        
    }
    
}
