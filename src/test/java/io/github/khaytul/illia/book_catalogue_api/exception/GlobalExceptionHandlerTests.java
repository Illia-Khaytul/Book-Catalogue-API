package io.github.khaytul.illia.book_catalogue_api.exception;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import io.github.khaytul.illia.book_catalogue_api.book.BookController;
import io.github.khaytul.illia.book_catalogue_api.book.BookService;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.InvalidPasswordException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.UserNotAuthenticatedException;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("GlobalExceptionHandler tests")
public class GlobalExceptionHandlerTests {

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return 409 Conflict when caught DuplicateEntryException")
    void shouldReturn409_whenCaughtDuplicateEntryException() throws Exception{
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
    void shouldReturn404_whenCaughtEntityNotFoundException() throws Exception{
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
    void shouldReturn400_whenCaughtInvalidPasswordException() throws Exception{
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
    void shouldReturn400_whenCaughtMethodArgumentNotValidException() throws Exception{
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
        .andExpect(jsonPath("$.message").value("Invalid request parameters"))
        .andExpect(jsonPath("$.data.titleContains").value("size must be between 1 and 100"))
        .andExpect(jsonPath("$.data.minPages").value("must be greater than or equal to 0"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when caught ConstraintViolationException")
    void shouldReturn400_whenCaughtConstraintViolationException() throws Exception{
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
    @DisplayName("Should return 409 Conflict when caught OptimisticLockingFailureException")
    void shouldReturn409_whenCaughtOptimisticLockingFailureException() throws Exception{
        //Arrange
        when(bookService.getBook(anyLong()))
            .thenThrow(new OptimisticLockingFailureException("message"));

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
    void shouldReturn401_whenCaughtUserNotAuthenticatedException() throws Exception{
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
        void shouldReturn409_whenUniqueConstraintViolation() throws Exception{
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
        void shouldReturn500_whenUnexpectedDataIntegrityViolationException() throws Exception{
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
    @DisplayName("Should return 400 Bad Request when caught HttpMessageNotReadableException")
    void shouldReturn400_whenCaughtHttpMessageNotReadableException() throws Exception{
        //Act and Assert
        mockMvc.perform(
            post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content("expected a json")
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
        .andExpect(jsonPath("$.message").value("Invalid request body"))
        .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 404 Not Found when caught NoResourceFoundException")
    void shouldReturn404_whenCaughtNoResourceFoundException() throws Exception{
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
    void shouldReturn500_whenCaughtUnexpectedException() throws Exception{
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
