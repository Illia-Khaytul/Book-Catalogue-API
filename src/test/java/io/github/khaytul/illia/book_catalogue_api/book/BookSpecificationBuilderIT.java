package io.github.khaytul.illia.book_catalogue_api.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul.illia.book_catalogue_api.common.pagination.PaginatedResponse;
import io.github.khaytul.illia.book_catalogue_api.config.SliceTestcontainersConfig;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BookService.class, BookSpecificationBuilder.class})
@ActiveProfiles("test")
@DisplayName("Book specifications integration tests")
public class BookSpecificationBuilderIT extends SliceTestcontainersConfig{

    @Autowired
    private BookService bookService;
    @Autowired
    private TestEntityManager entityManager;

    private final Pageable pagination = PageRequest.ofSize(10);
    private Book book1;
    private Book book2;
    private Book book3;

    @BeforeEach
    void beforeEach(){
        book1 = new Book(
            null,
            "Cool Book Vol.1",
            null,
            "Original Author",
            100,
            LocalDate.parse("2025-06-06"),
            null
        );
        book2 = new Book(
            null,
            "Cool Book Vol.2",
            null,
            "Original Author",
            125,
            LocalDate.parse("2023-06-06"),
            null
        );
        book3 = new Book(
            null,
            "Different Book",
            null,
            "Different Author",
            200,
            LocalDate.parse("2020-06-06"),
            null
        );

        book1 = entityManager.persist(book1);
        book2 = entityManager.persist(book2);
        book3 = entityManager.persist(book3);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should return all books when BookFiltering is empty")
    void shouldReturnAllBooks_whenFilteringEmpty(){
        //Arrange
        BookFiltering filtering = new BookFiltering(null, null, null, null, null, null);

        //Act
        PaginatedResponse<BookResponse> response = bookService.getBooks(pagination, filtering);

        //Assert
        assertThat(response).isNotNull();
        List<BookResponse> books = response.content();
        assertThat(books).hasSize(3);
        assertThat(books.stream().map(book -> book.id())).containsExactlyInAnyOrder(book1.getId(), book2.getId(), book3.getId());
    }
    
    @Test
    @DisplayName("Should return books where title contains titleContains value")
    void shouldReturnBooksWhereTitleContainsValue(){
        //Arrange
        BookFiltering filtering = new BookFiltering("Cool", null, null, null, null, null);

        //Act
        PaginatedResponse<BookResponse> response = bookService.getBooks(pagination, filtering);

        //Assert
        assertThat(response).isNotNull();
        List<BookResponse> books = response.content();
        assertThat(books).hasSize(2);
        assertThat(books.stream().map(book -> book.id())).containsExactlyInAnyOrder(book1.getId(), book2.getId());
    }
    
    @Test
    @DisplayName("Should return books where author contains authorName value")
    void shouldReturnBooksWhereAuthorNameContainsValue(){
        //Arrange
        BookFiltering filtering = new BookFiltering(null, "Different", null, null, null, null);

        //Act
        PaginatedResponse<BookResponse> response = bookService.getBooks(pagination, filtering);

        //Assert
        assertThat(response).isNotNull();
        List<BookResponse> books = response.content();
        assertThat(books).hasSize(1);
        assertThat(books.stream().map(book -> book.id())).containsExactlyInAnyOrder(book3.getId());
    }
    
    @Test
    @DisplayName("Should return books where pages is equal or greater than value")
    void shouldReturnBooksWithMinPages(){
        //Arrange
        BookFiltering filtering = new BookFiltering(null, null, book2.getPages(), null, null, null);

        //Act
        PaginatedResponse<BookResponse> response = bookService.getBooks(pagination, filtering);

        //Assert
        assertThat(response).isNotNull();
        List<BookResponse> books = response.content();
        assertThat(books).hasSize(2);
        assertThat(books.stream().map(book -> book.id())).containsExactlyInAnyOrder(book2.getId(), book3.getId());
    }
    
    @Test
    @DisplayName("Should return books where pages is equal or less value")
    void shouldReturnBooksWithMaxPages(){
        //Arrange
        BookFiltering filtering = new BookFiltering(null, null, null, book2.getPages(), null, null);

        //Act
        PaginatedResponse<BookResponse> response = bookService.getBooks(pagination, filtering);

        //Assert
        assertThat(response).isNotNull();
        List<BookResponse> books = response.content();
        assertThat(books).hasSize(2);
        assertThat(books.stream().map(book -> book.id())).containsExactlyInAnyOrder(book1.getId(), book2.getId());
    }
    
    @Test
    @DisplayName("Should return books where release date is same or after date value")
    void shouldReturnBooksReleasedAfterDate(){
        //Arrange
        BookFiltering filtering = new BookFiltering(null, null, null, null, null, book2.getReleaseDate());

        //Act
        PaginatedResponse<BookResponse> response = bookService.getBooks(pagination, filtering);

        //Assert
        assertThat(response).isNotNull();
        List<BookResponse> books = response.content();
        assertThat(books).hasSize(2);
        assertThat(books.stream().map(book -> book.id())).containsExactlyInAnyOrder(book1.getId(), book2.getId());
    }
    
    @Test
    @DisplayName("Should return books where release date is same or before date value")
    void shouldReturnBooksReleasedBeforeDate(){
        //Arrange
        BookFiltering filtering = new BookFiltering(null, null, null, null, book2.getReleaseDate(), null);

        //Act
        PaginatedResponse<BookResponse> response = bookService.getBooks(pagination, filtering);

        //Assert
        assertThat(response).isNotNull();
        List<BookResponse> books = response.content();
        assertThat(books).hasSize(2);
        assertThat(books.stream().map(book -> book.id())).containsExactlyInAnyOrder(book2.getId(), book3.getId());
    }
    
    @Test
    @DisplayName("Should return books with min pages and released after date value")
    void shouldReturnBooksByMinPagesAndReleasedAfter(){
        //Arrange
        BookFiltering filtering = new BookFiltering(null, null, book2.getPages(), null, null, book2.getReleaseDate());

        //Act
        PaginatedResponse<BookResponse> response = bookService.getBooks(pagination, filtering);

        //Assert
        assertThat(response).isNotNull();
        List<BookResponse> books = response.content();
        assertThat(books).hasSize(1);
        assertThat(books.stream().map(book -> book.id())).containsExactlyInAnyOrder(book2.getId());
    }
    
}
