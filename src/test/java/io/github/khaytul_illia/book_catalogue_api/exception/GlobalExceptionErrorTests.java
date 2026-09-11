package io.github.khaytul_illia.book_catalogue_api.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DummyController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("GlobalErrorHandler tests")
public class GlobalExceptionErrorTests {

    @MockitoSpyBean
    private DummyController dummyController;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 409 Conflict when caught DuplicateEntryException")
    void shouldReturn409_whenCaughtDuplicateEntryException() throws Exception{
        //Arrange
        DuplicateEntryException exception = new DuplicateEntryException("message");

        doThrow(exception)
            .when(dummyController).dummyOperation();

        //Act and Assert
        mockMvc.perform(
                get("/dummy/{id}", 1)
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

        doThrow(exception)
            .when(dummyController).dummyOperation();

        //Act and Assert
        mockMvc.perform(
                get("/dummy/{id}", 1)
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

        doThrow(exception)
            .when(dummyController).dummyOperation();

        //Act and Assert
        mockMvc.perform(
                get("/dummy/{id}", 1)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").value(exception.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when caught UserNotAuthenticatedException")
    void shouldReturn401_whenCaughtUserNotAuthenticatedException() throws Exception{
        //Arrange
        doThrow(new UserNotAuthenticatedException("message"))
            .when(dummyController).dummyOperation();

        //Act and Assert
        mockMvc.perform(
                get("/dummy/{id}", 1)
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
            .andExpect(jsonPath("$.message").value("User is not authenticated"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when caught MethodArgumentNotValidException")
    void shouldReturn400_whenCaughtMethodArgumentNotValidException() throws Exception {
        //Act and Assert
        mockMvc.perform(
                post("/dummy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new DummyController.DummyRequestBody(null)))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").value("Invalid request parameters"))
            .andExpect(jsonPath("$.data.value").value("must not be null"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when caught HandlerMethodValidationException")
    void shouldReturn400_whenCaughtHandlerMethodValidationException() throws Exception {
        //Act and Assert
        mockMvc.perform(
                get("/dummy/{id}", -1)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").value("Invalid request parameters"))
            .andExpect(jsonPath("$.data.id").value("must be greater than 0"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when caught HttpMessageNotReadableException")
    void shouldReturn400_whenCaughtHttpMessageNotReadableException() throws Exception {
        //Act and Assert
        mockMvc.perform(
                post("/dummy")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("not a json")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").value("Invalid request body"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 409 Conflict when caught OptimisticLockingFailureException")
    void shouldReturn409_whenCaughtOptimisticLockingFailureException() throws Exception{
        //Arrange
        doThrow(new OptimisticLockingFailureException("message"))
            .when(dummyController).dummyOperation();

        //Act and Assert
        mockMvc.perform(
                get("/dummy/{id}", 1)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_CONFLICT))
            .andExpect(jsonPath("$.message").value("Concurrent modification error"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should return 404 Not Found when caught NoResourceFoundException")
    void shouldReturn404_whenCaughtNoResourceFoundException() throws Exception {
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
    void shouldReturn500_whenCaughtUnexpectedException() throws Exception {
        //Arrange
        doThrow(new RuntimeException())
            .when(dummyController).dummyOperation();

        //Act and Assert
        mockMvc.perform(
                get("/dummy/{id}", 1)
            )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_INTERNAL_SERVER_ERROR))
            .andExpect(jsonPath("$.message").value("Something went wrong"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

}
