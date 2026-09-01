package io.github.khaytul.illia.book_catalogue_api.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul.illia.book_catalogue_api.common.pagination.PaginatedResponse;
import io.github.khaytul.illia.book_catalogue_api.config.TestcontainersConfig;
import io.github.khaytul.illia.book_catalogue_api.exception.ErrorResponse;
import io.github.khaytul.illia.book_catalogue_api.user.User;
import io.github.khaytul.illia.book_catalogue_api.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Book end to end api tests")
public class BookApiIT { 

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTestClient restClient;

    @LocalServerPort
    private int port;

    private final String password = "password";
    private String passwordHash;
    private final String username = "username";
    private HttpHeaders headers;

    @BeforeAll
    void beforeAll(){
        passwordHash = passwordEncoder.encode(password);
        
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);
    }

    @BeforeEach
    void beforeEach(){
        User user = new User(null, username, passwordHash);

        userRepository.save(user);
    }

    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Nested
    @DisplayName("Happy path tests")
    class HappyPathTests{

        @Test
        @DisplayName("Should create new book")
        void shouldCreateNewBook(){
            //Arrange
            BookCreateRequest request = new BookCreateRequest(
                "Cool Book Vol.1",
                null,
                "Original Author",
                100,
                LocalDate.parse("2025-06-06")
            );

            //Act and Assert
            restClient
                .post()
                .uri("/books")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .body(request)
            .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", String.format("http://localhost:%s/books/\\d+", port))
                .expectBody(BookResponse.class).value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.title()).isEqualTo(request.title());
                    assertThat(response.description()).isEqualTo(request.description());
                    assertThat(response.author()).isEqualTo(request.author());
                    assertThat(response.pages()).isEqualTo(request.pages());
                    assertThat(response.releaseDate()).isEqualTo(request.releaseDate());

                    assertThat(bookRepository.existsById(response.id())).isTrue();
                });
        }

        @Test
        @DisplayName("Should update existing book")
        void shouldUpdateExistingBook(){
            //Arrange
            long bookId;
            Book book = new Book(
                null,
                "Cool Book Vol.1",
                null,
                "Original Author",
                100,
                LocalDate.parse("2025-06-06"),
                null
            );
            BookUpdateRequest request = new BookUpdateRequest(null, null, null, 1, null);

            bookId = bookRepository.save(book).getId();

            //Act and Assert
            restClient
                .patch()
                .uri("/books/{bookId}", bookId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .body(request)
            .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponse.class).value(response -> {
                    assertThat(response.id()).isEqualTo(bookId);
                    assertThat(response.title()).isEqualTo(book.getTitle());
                    assertThat(response.description()).isEqualTo(book.getDescription());
                    assertThat(response.author()).isEqualTo(book.getAuthor());
                    assertThat(response.pages()).isEqualTo(request.pages());
                    assertThat(response.releaseDate()).isEqualTo(book.getReleaseDate());
                });
        }
        
        @Test
        @DisplayName("Should get existing book")
        void shouldGetExistingBook(){
            //Arrange
            long bookId;
            Book book = new Book(
                null,
                "Cool Book Vol.1",
                null,
                "Original Author",
                100,
                LocalDate.parse("2025-06-06"),
                null
            );

            bookId = bookRepository.save(book).getId();

            //Act and Assert
            restClient
                .get()
                .uri("/books/{bookId}", bookId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
            .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponse.class).value(response -> {
                    assertThat(response.id()).isEqualTo(bookId);
                    assertThat(response.title()).isEqualTo(book.getTitle());
                    assertThat(response.description()).isEqualTo(book.getDescription());
                    assertThat(response.author()).isEqualTo(book.getAuthor());
                    assertThat(response.pages()).isEqualTo(book.getPages());
                    assertThat(response.releaseDate()).isEqualTo(book.getReleaseDate());
                });
        }
        
        @Test
        @DisplayName("Should get a page of books")
        void shouldGetAPageOfBooks(){
            //Arrange
            int page = 0;
            int pageSize = 2;
            Book book1 = new Book(null, "book1", null, "same author", null, null, null);
            Book book2 = new Book(null, "book2", null, "same author", null, null, null);
            Book book3 = new Book(null, "book3", null, "same author", null, null, null);
            ParameterizedTypeReference<PaginatedResponse<BookResponse>> responseType = new ParameterizedTypeReference<>() {};

            List<Book> books = bookRepository.saveAll(List.of(book1, book2, book3));

            //Act and Assert
            restClient
                .get()
                .uri(UriComponentsBuilder
                    .fromPath("/books")
                    .queryParam("page", page)
                    .queryParam("size", pageSize)
                    .build()
                    .toUri()
                )
                .headers(httpHeaders -> httpHeaders.putAll(headers))
            .exchange()
                .expectStatus().isOk()
                .expectBody(responseType).value(response -> {
                    assertThat(response.page()).isEqualTo(page);
                    assertThat(response.totalPages()).isEqualTo(books.size() / pageSize + 1);
                    assertThat(response.pageSize()).isEqualTo(pageSize);
                    assertThat(response.totalElements()).isEqualTo(books.size());
                    assertThat(response.content().stream().map(book -> book.id()))
                        .containsAnyElementsOf(books.stream().map(book -> book.getId()).toList());
                });
        }
        
        @Test
        @DisplayName("Should delete existing book")
        void shouldDeleteExistingBook(){
            //Arrange
            long bookId;
            Book book = new Book(
                null,
                "Cool Book Vol.1",
                null,
                "Original Author",
                100,
                LocalDate.parse("2025-06-06"),
                null
            );

            book = bookRepository.save(book);
            bookId = book.getId();

            //Act and Assert
            restClient
                .delete()
                .uri("/books/{bookId}", bookId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
            .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
            
            assertThat(bookRepository.existsById(bookId)).isFalse();
        }

    }
    
    @Nested
    @DisplayName("Error path tests")
    class ErrorPathTests{
        
        @Test
        @DisplayName("Should return 409 Conflict when book already exists")
        void shouldReturn409_whenCreatingExistingBook(){
            //Arrange
            Book book = new Book(
                null,
                "Cool Book Vol.1",
                null,
                "Original Author",
                100,
                LocalDate.parse("2025-06-06"),
                null
            );
            BookCreateRequest request = new BookCreateRequest(
                "Cool Book Vol.1",
                null,
                "Original Author",
                null,
                null
            );
            
            bookRepository.save(book).getId();

            //Act and Assert
            restClient
                .post()
                .uri("/books")
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .body(request)
            .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response.timestamp()).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpServletResponse.SC_CONFLICT);
                    assertThat(response.message()).isEqualTo(String.format("A book with title '%s' by '%s' already exists", request.title(), request.author()));
                    assertThat(response.data()).isEmpty();
                });
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when accessing with no authentication")
        void shouldReturn401_whenNoAuthentication(){
            //Arrange
            long bookId = 1;
            BookUpdateRequest request = new BookUpdateRequest(null, null, null, 1, null);

            //Act and Assert
            restClient
                .patch()
                .uri("/books/{bookId}", bookId)
                .body(request)
            .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response.timestamp()).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
                    assertThat(response.message()).isEqualTo("Full authentication is required to access this resource");
                    assertThat(response.data()).isEmpty();
                });
        }
        
        @Test
        @DisplayName("Should return 404 Not Found when book does not exist")
        void shouldReturn404_whenBookDoesNotExist(){
            //Arrange
            long bookId = 1;

            //Act and Assert
            restClient
                .get()
                .uri("/books/{bookId}", bookId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
            .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response.timestamp()).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
                    assertThat(response.message()).isEqualTo(String.format("Book with id '%s' does not exist", bookId));
                    assertThat(response.data()).isEmpty();
                });
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when invalid request parameters")
        void shouldReturn400_whenInvalidRequest(){
            //Arrange
            String titleContains = "";
            int minPages = -1;

            //Act and Assert
            restClient
                .get()
                .uri(UriComponentsBuilder
                    .fromPath("/books")
                    .queryParam("titleContains", titleContains)
                    .queryParam("minPages", minPages)
                    .build()
                    .toUri()
                )
                .headers(httpHeaders -> httpHeaders.putAll(headers))
            .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class).value(response -> {
                    assertThat(response.timestamp()).isNotNull();
                    assertThat(response.status()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
                    assertThat(response.message()).isEqualTo("Invalid request parameters");
                    assertThat(response.data().get("titleContains")).isEqualTo("size must be between 1 and 100");
                    assertThat(response.data().get("minPages")).isEqualTo("must be greater than or equal to 0");
                });
        }
        
    }
    
}
