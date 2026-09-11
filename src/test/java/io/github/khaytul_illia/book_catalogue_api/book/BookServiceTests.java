package io.github.khaytul_illia.book_catalogue_api.book;

import io.github.khaytul_illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul_illia.book_catalogue_api.exception.DuplicateEntryException;
import io.github.khaytul_illia.book_catalogue_api.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

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

    @Nested
    @DisplayName("updateBook tests")
    class UpdateBookTests{

        private final long bookId = 1;
        private final BookUpdateRequest request = new BookUpdateRequest(
            "New title",
            null,
            null,
            null,
            LocalDate.parse("2020-01-01")
        );
        private final Book foundBook = new Book(
            bookId,
            "Cool Book Vol.1",
            "Lorem ipsum dolor sit amet",
            "Not An Author",
            200,
            LocalDate.parse("2020-08-10"),
            1
        );

        @Test
        @DisplayName("Should throw EntityNotFoundException when book not found by id")
        void shouldThrowEntityNotFoundException_whenBookIsNotFound(){
            //Arrange
            when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

            //Act and Assert
            assertThatThrownBy(() -> bookService.updateBook(bookId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book with id '%s' does not exist", bookId);

            verify(bookRepository).findById(bookId);
            verify(bookRepository, never()).existsByTitleAndAuthor(request.title(), foundBook.getAuthor());
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should return found book response when request is empty")
        void shouldReturnFoundBookResponse_whenRequestIsEmpty(){
            //Arrange
            BookUpdateRequest request = new BookUpdateRequest(null, null, null, null, null);

            when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(foundBook));

            //Act
            BookResponse response = bookService.updateBook(bookId, request);

            //Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(foundBook.getId());
            assertThat(response.title()).isEqualTo(foundBook.getTitle());
            assertThat(response.description()).isEqualTo(foundBook.getDescription());
            assertThat(response.author()).isEqualTo(foundBook.getAuthor());
            assertThat(response.pages()).isEqualTo(foundBook.getPages());
            assertThat(response.releaseDate()).isEqualTo(foundBook.getReleaseDate());

            verify(bookRepository).findById(bookId);
            verify(bookRepository, never()).existsByTitleAndAuthor(request.title(), foundBook.getAuthor());
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should throw DuplicateEntryException when updated title and author are not unique")
        void shouldThrowDuplicateEntryException_whenUpdatedBookAlreadyExists(){
            //Arrange
            when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(foundBook));
            when(bookRepository.existsByTitleAndAuthor(request.title(), foundBook.getAuthor()))
                .thenReturn(true);

            //Act and Assert
            assertThatThrownBy(() -> bookService.updateBook(bookId, request))
                .isInstanceOf(DuplicateEntryException.class)
                .hasMessageContaining("A book with title '%s' by '%s' already exists", request.title(), foundBook.getAuthor());

            verify(bookRepository).findById(bookId);
            verify(bookRepository).existsByTitleAndAuthor(request.title(), foundBook.getAuthor());
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should not check for duplicates when title and author do not change")
        void shouldNotVerifyDuplicateExistence_whenUpdatedBookTitleAndAuthorDoNotChange(){
            //Arrange
            BookUpdateRequest request = new BookUpdateRequest(null, null, null, 0, null);

            when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(foundBook));
            when(bookRepository.save(any(Book.class)))
                .thenReturn(new Book());

            //Act
            bookService.updateBook(bookId, request);

            //Assert
            verify(bookRepository).findById(bookId);
            verify(bookRepository, never()).existsByTitleAndAuthor(foundBook.getTitle(), foundBook.getAuthor());
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("Should update and save book when title and author are unique")
        void shouldSaveAndReturnResponse_whenUpdatedBookDoesNotExist(){
            //Arrange
            when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(foundBook));
            when(bookRepository.existsByTitleAndAuthor(request.title(), foundBook.getAuthor()))
                .thenReturn(false);
            when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            //Act
            BookResponse response = bookService.updateBook(bookId, request);

            //Assert
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(foundBook.getId());
            assertThat(response.title()).isEqualTo(request.title());
            assertThat(response.description()).isEqualTo(foundBook.getDescription());
            assertThat(response.author()).isEqualTo(foundBook.getAuthor());
            assertThat(response.pages()).isEqualTo(foundBook.getPages());
            assertThat(response.releaseDate()).isEqualTo(request.releaseDate());

            verify(bookRepository).findById(bookId);
            verify(bookRepository).existsByTitleAndAuthor(request.title(), foundBook.getAuthor());
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("Should update found book with request before saving it")
        void shouldUpdateBookWithRequest(){
            //Arrange
            when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(foundBook));
            when(bookRepository.existsByTitleAndAuthor(request.title(), foundBook.getAuthor()))
                .thenReturn(false);
            when(bookRepository.save(any(Book.class)))
                .thenReturn(new Book());

            //Act
            bookService.updateBook(bookId, request);

            //Assert
            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
            verify(bookRepository).save(bookCaptor.capture());

            Book book = bookCaptor.getValue();
            assertThat(book.getId()).isEqualTo(foundBook.getId());
            assertThat(book.getTitle()).isEqualTo(request.title());
            assertThat(book.getDescription()).isEqualTo(foundBook.getDescription());
            assertThat(book.getAuthor()).isEqualTo(foundBook.getAuthor());
            assertThat(book.getPages()).isEqualTo(foundBook.getPages());
            assertThat(book.getReleaseDate()).isEqualTo(request.releaseDate());
        }

    }

}
