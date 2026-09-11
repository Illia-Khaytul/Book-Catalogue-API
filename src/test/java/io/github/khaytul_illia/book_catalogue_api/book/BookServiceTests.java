package io.github.khaytul_illia.book_catalogue_api.book;

import io.github.khaytul_illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul_illia.book_catalogue_api.exception.DuplicateEntryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService tests")
public class BookServiceTests {

    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;

    @Nested
    @DisplayName("createBook tests")
    class CreateBookTests{

        private final BookCreateRequest request = new BookCreateRequest(
            "Cool Book Vol.1",
            "Lorem ipsum dolor sit amet",
            "Not An Author",
            200,
            LocalDate.parse("2020-08-10")
        );

        @Test
        @DisplayName("Should throw DuplicateEntryException when title and author are not unique")
        void shouldThrowDuplicateEntryException_whenBookAlreadyExists() {
            //Arrange
            when(bookRepository.existsByTitleAndAuthor(request.title(), request.author()))
                .thenReturn(true);

            //Act and Assert
            assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(DuplicateEntryException.class)
                .hasMessageContaining("A book with title '%s' by '%s' already exists", request.title(), request.author());

            verify(bookRepository).existsByTitleAndAuthor(request.title(), request.author());
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should create and save book when title and author are unique")
        void shouldSaveAndReturnResponse_whenBookDoesNotExist(){
            //Arrange
            Book savedBook = new Book(
                1L,
                request.title(),
                request.description(),
                request.author(),
                request.pages(),
                request.releaseDate(),
                1
            );

            when(bookRepository.existsByTitleAndAuthor(request.title(), request.author()))
                .thenReturn(false);
            when(bookRepository.save(any(Book.class)))
                .thenReturn(savedBook);

            //Act
            BookResponse response = bookService.createBook(request);

            //Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(savedBook.getId());
            assertThat(response.title()).isEqualTo(savedBook.getTitle());
            assertThat(response.description()).isEqualTo(savedBook.getDescription());
            assertThat(response.author()).isEqualTo(savedBook.getAuthor());
            assertThat(response.pages()).isEqualTo(savedBook.getPages());
            assertThat(response.releaseDate()).isEqualTo(savedBook.getReleaseDate());

            verify(bookRepository).existsByTitleAndAuthor(request.title(), request.author());
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("Should map the request to the book entity before saving it")
        void shouldMapRequestToEntity() {
            //Arrange
            when(bookRepository.existsByTitleAndAuthor(request.title(), request.author()))
                .thenReturn(false);
            when(bookRepository.save(any(Book.class)))
                .thenReturn(new Book());

            //Act
            bookService.createBook(request);

            //Assert
            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
            verify(bookRepository).save(bookCaptor.capture());

            Book book = bookCaptor.getValue();
            assertThat(book.getId()).isNull();
            assertThat(book.getTitle()).isEqualTo(request.title());
            assertThat(book.getDescription()).isEqualTo(request.description());
            assertThat(book.getAuthor()).isEqualTo(request.author());
            assertThat(book.getPages()).isEqualTo(request.pages());
            assertThat(book.getReleaseDate()).isEqualTo(request.releaseDate());
        }

    }

}
