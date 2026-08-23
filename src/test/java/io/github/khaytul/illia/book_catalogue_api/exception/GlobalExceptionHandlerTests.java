package io.github.khaytul.illia.book_catalogue_api.exception;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import io.github.khaytul.illia.book_catalogue_api.book.BookController;
import io.github.khaytul.illia.book_catalogue_api.book.BookService;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.InvalidPasswordException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.UserNotAuthenticatedException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("GlobalExceptionHandler tests")
public class GlobalExceptionHandlerTests {

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return 409 Conflict when caught DuplicateEntryException")
    public void whenCaughtDuplicateEntryException_shouldReturn409() throws Exception{
        //Arrange
        DuplicateEntryException exception = new DuplicateEntryException("message");

        when(bookService.getBook(anyLong()))
            .thenThrow(exception);

        //Act and Assert
        mockMvc.perform(
            get("/books/{bookId}", 1)
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_CONFLICT))
        .andExpect(jsonPath("$.message").value(exception.getMessage()))
        .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 404 Not Found when caught EntityNotFoundException")
    public void whenCaughtEntityNotFoundException_shouldReturn404() throws Exception{
        //Arrange
        EntityNotFoundException exception = new EntityNotFoundException("message");

        when(bookService.getBook(anyLong()))
            .thenThrow(exception);

        //Act and Assert
        mockMvc.perform(
            get("/books/{bookId}", 1)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_NOT_FOUND))
        .andExpect(jsonPath("$.message").value(exception.getMessage()))
        .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when caught InvalidPasswordException")
    public void whenCaughtInvalidPasswordException_shouldReturn400() throws Exception{
        //Arrange
        InvalidPasswordException exception = new InvalidPasswordException("message");

        when(bookService.getBook(anyLong()))
            .thenThrow(exception);

        //Act and Assert
        mockMvc.perform(
            get("/books/{bookId}", 1)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
        .andExpect(jsonPath("$.message").value(exception.getMessage()))
        .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when caught MethodArgumentNotValidException")
    public void whenCaughtMethodArgumentNotValidException_shouldReturn400() throws Exception{
        //Act and Assert
        mockMvc.perform(
            get("/books")
            .queryParams(MultiValueMap.fromSingleValue(Map.of(
                "titleContains", "",
                "minPages", "-1"
            )))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
        .andExpect(jsonPath("$.message").value("Invalid payload parameters"))
        .andExpect(jsonPath("$.data.titleContains").value("size must be between 1 and 100"))
        .andExpect(jsonPath("$.data.minPages").value("must be greater than or equal to 0"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when caught ConstraintViolationException")
    public void whenCaughtConstraintViolationException_shouldReturn400() throws Exception{
        //Act and Assert
        mockMvc.perform(
            get("/books/{bookId}", -1)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
        .andExpect(jsonPath("$.message").value("Invalid request parameters"))
        .andExpect(jsonPath("$.data.bookId").value("must be greater than 0"));
    }

    @Test
    @DisplayName("Should return 409 Conflict when caught OptimisticLockException")
    public void whenCaughtOptimisticLockException_shouldReturn409() throws Exception{
        //Arrange
        when(bookService.getBook(anyLong()))
            .thenThrow(new OptimisticLockException());

        //Act and Assert
        mockMvc.perform(
            get("/books/{bookId}", 1)
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_CONFLICT))
        .andExpect(jsonPath("$.message").value("Concurrent modification error"))
        .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when caught UserNotAuthenticatedException")
    public void whenCaughtUserNotAuthenticatedException_shouldReturn401() throws Exception{
        //Arrange
        when(bookService.getBook(anyLong()))
            .thenThrow(new UserNotAuthenticatedException("message"));

        //Act and Assert
        mockMvc.perform(
            get("/books/{bookId}", 1)
        )
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
        .andExpect(jsonPath("$.message").value("User is not authenticated"))
        .andExpect(jsonPath("$.data").isEmpty());
    }

    @Nested
    @DisplayName("dataIntegrityViolationHandler tests")
    class DataIntegrityViolationHandlerTests{

        @Test
        @DisplayName("Should return 409 Conflict when a unique constraint is violated")
        public void whenUniqueConstraintViolation_shouldReturn409() throws Exception{
            //Arrange
            ConstraintViolationException cve = new ConstraintViolationException("message", new SQLException(), "unique_constraint");

            when(bookService.getBook(anyLong()))
                .thenThrow(new DataIntegrityViolationException("message", cve));

            //Act and Assert
            mockMvc.perform(
                get("/books/{bookId}", 1)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_CONFLICT))
            .andExpect(jsonPath("$.message").value("Unique constraint violation"))
            .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("Should return 500 Internal Server Error when caught unexpected DataIntegrityViolationException")
        public void whenCaughtOptimisticLockException_shouldReturn409() throws Exception{
            //Arrange
            when(bookService.getBook(anyLong()))
                .thenThrow(new DataIntegrityViolationException("message", new RuntimeException()));

            //Act and Assert
            mockMvc.perform(
                get("/books/{bookId}", 1)
            )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_INTERNAL_SERVER_ERROR))
            .andExpect(jsonPath("$.message").value("Something went wrong"))
            .andExpect(jsonPath("$.data").isEmpty());
        }

    }

    @Test
    @DisplayName("Should return 404 Not Found when caught NoResourceFoundException")
    public void whenCaughtNoResourceFoundException_shouldReturn404() throws Exception{
        //Act and Assert
        mockMvc.perform(
            get("/non-existing-mapping")
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_NOT_FOUND))
        .andExpect(jsonPath("$.message").value("Resource not found"))
        .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when caught unexpected Exception")
    public void whenCaughtUnexpectedException_shouldReturn500() throws Exception{
        //Arrange
        when(bookService.getBook(anyLong()))
            .thenThrow(new RuntimeException());

        //Act and Assert
        mockMvc.perform(
            get("/books/{bookId}", 1)
        )
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_INTERNAL_SERVER_ERROR))
        .andExpect(jsonPath("$.message").value("Something went wrong"))
        .andExpect(jsonPath("$.data").isEmpty());
    }
    
}
