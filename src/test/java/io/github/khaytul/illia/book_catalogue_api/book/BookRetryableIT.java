package io.github.khaytul.illia.book_catalogue_api.book;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul.illia.book_catalogue_api.config.TestcontainersConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfig.class)
@ActiveProfiles("test")
@DisplayName("Book update retry logic integration tests")
public class BookRetryableIT {

    @MockitoBean
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    private Book original;
    private BookUpdateRequest request;

    @BeforeEach
    public void beforeEach(){
        //Arrange
        original = new Book(1L, "Cool Book Vol.1", null, "Original Author", null, null, null);
        request = new BookUpdateRequest(null, null, null, 0, null);

        when(bookRepository.findById(original.getId()))
            .thenReturn(Optional.of(original));
    }

    @Test
    @DisplayName("Should succeed when no OptimisticLockingFailureException is thrown")
    public void shouldSucceed_whenNoRetries(){
        //Arrange
        when(bookRepository.save(any(Book.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        bookService.updateBook(original.getId(), request);

        //Assert
        verify(bookRepository).findById(original.getId());
        verify(bookRepository).save(any(Book.class));
    }
    
    @Test
    @DisplayName("Should succeed when OptimisticLockingFailureException is thrown")
    public void shouldSucceed_whenOptimisticLockingFailureExceptionIsThrown(){
        //Arrange
        when(bookRepository.save(any(Book.class)))
            .thenThrow(new OptimisticLockingFailureException("message"))
            .thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        bookService.updateBook(original.getId(), request);

        //Assert
        verify(bookRepository, times(2)).findById(original.getId());
        verify(bookRepository, times(2)).save(any(Book.class));
    }
    
    @Test
    @DisplayName("Should throw OptimisticLockingFailureException when no more retries available")
    public void shouldThrowOptimisticLockingFailureException_whenAllRetriesUsed(){
        //Arrange
        when(bookRepository.save(any(Book.class)))
            .thenThrow(new OptimisticLockingFailureException("message"));

        //Act and Assert
        assertThatThrownBy(() -> bookService.updateBook(original.getId(), request))
            .isInstanceOf(OptimisticLockingFailureException.class);

        //Assert
        verify(bookRepository, times(4)).findById(original.getId());
        verify(bookRepository, times(4)).save(any(Book.class));
    }
    
}
