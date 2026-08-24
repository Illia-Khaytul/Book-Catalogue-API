package io.github.khaytul.illia.book_catalogue_api.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import io.github.khaytul.illia.book_catalogue_api.config.TestcontainersConfig;
import io.github.khaytul.illia.book_catalogue_api.exception.ErrorResponse;
import io.github.khaytul.illia.book_catalogue_api.user.request.PasswordChangeRequest;
import io.github.khaytul.illia.book_catalogue_api.user.request.UserCreateRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("User end to end api tests")
public class UserApiIT {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTestClient restClient;

    private final String password = "password";
    private String passwordHash;
    private final String username = "username";
    private HttpHeaders headers;

    @BeforeAll
    public void beforeAll(){
        passwordHash = passwordEncoder.encode(password);
        
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);
    }

    @BeforeEach
    public void beforeEach(){
        User user = new User(null, username, passwordHash);

        userRepository.save(user);
    }

    @AfterEach
    public void afterEach(){
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Happy path tests")
    class HappyPathTests{

        @Test
        @DisplayName("Should create new user")
        public void shouldCreateNewUser(){
            //Arrange
            UserCreateRequest request = new UserCreateRequest("newUser", password);

            //Act and Assert
            restClient
                .post()
                .uri("/users")
                .body(request)
            .exchange()
                .expectStatus().isCreated()
                .expectBody(Void.class);
            
            assertThat(userRepository.existsByUsername(username)).isTrue();
        }
        
        @Test
        @DisplayName("Should change authenticated user password")
        public void shouldChangeCurrentUserPassword(){
            //Arrange
            String newPassword = "newPassword";
            PasswordChangeRequest request = new PasswordChangeRequest(password, newPassword);

            //Act and Assert
            restClient
                .patch()
                .uri("/users/password")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .body(request)
            .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
            
            Optional<User> changedUser = userRepository.findByUsername(username);
            assertThat(changedUser).isNotEmpty();
            assertThat(passwordEncoder.matches(newPassword, changedUser.get().getPassword())).isTrue();
        }

        @Test
        @DisplayName("Should delete authenticated user")
        public void shouldDeleteCurrentUser(){
            //Act and Assert
            restClient
                .delete()
                .uri("/users")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
            .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
            
            assertThat(userRepository.existsByUsername(username)).isFalse();
        }
        
    }

    @Nested
    @DisplayName("Error path tests")
    class ErrorPathTests{
        
        @Test
        @DisplayName("Should return 409 Conflict when user already exists")
        public void shouldReturn409_whenCreatingExistingUser(){
            //Arrange
            UserCreateRequest request = new UserCreateRequest(username, password);

            //Act and Assert
            restClient
                .post()
                .uri("/users")
                .body(request)
            .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response.status()).isEqualTo(HttpServletResponse.SC_CONFLICT);
                });
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when passing invalid password")
        public void shouldReturn400_whenInvalidPassword(){
            //Arrange
            PasswordChangeRequest request = new PasswordChangeRequest(password, password);

            //Act and Assert
            restClient
                .patch()
                .uri("/users/password")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .body(request)
            .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response.status()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
                });
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when accessing with no authentication")
        public void shouldReturn401_whenNoAuthentication(){
            //Act and Assert
            restClient
                .delete()
                .uri("/users")
            .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response.status()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
                });
        }

    }

}
