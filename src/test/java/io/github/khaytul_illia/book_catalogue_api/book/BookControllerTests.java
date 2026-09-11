package io.github.khaytul_illia.book_catalogue_api.book;

import io.github.khaytul_illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.response.BookResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("BookController tests")
public class BookControllerTests {

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("createBook tests")
    class CreateBookTests{

        @Test
        @DisplayName("Should return 201 Created when request is valid")
        void shouldReturn201_whenValidRequest() throws Exception{
            //Arrange
            BookCreateRequest request = new BookCreateRequest(
                "Cool Book Vol.1",
                null,
                "Not An Author",
                null,
                null
            );
            BookResponse response = new BookResponse(
                1L,
                "Cool Book Vol.1",
                null,
                "Not An Author",
                null,
                null
            );

            when(bookService.createBook(any(BookCreateRequest.class)))
                .thenReturn(response);

            //Act and Assert
            mockMvc.perform(
                    post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("http://localhost/books/%s", response.id())))
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.title").value(response.title()))
                .andExpect(jsonPath("$.author").value(response.author()));

            verify(bookService).createBook(any(BookCreateRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when required request fields are missing")
        void shouldReturn400_whenRequestRequiredFieldsMissing() throws Exception{
            //Arrange
            BookCreateRequest request = new BookCreateRequest(null, null, null, null, null);

            //Act and Assert
            mockMvc.perform(
                    post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
                .andExpect(jsonPath("$.data.title").value("must not be null"))
                .andExpect(jsonPath("$.data.author").value("must not be null"));

            verify(bookService, never()).createBook(any(BookCreateRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request fields are invalid")
        void shouldReturn400_whenRequestInvalid() throws Exception{
            //Arrange
            BookCreateRequest request = new BookCreateRequest(
                "",
                null,
                "",
                -1,
                LocalDate.now().plusYears(1)
            );

            //Act and Assert
            mockMvc.perform(
                    post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
                .andExpect(jsonPath("$.data.title").value("size must be between 1 and 100"))
                .andExpect(jsonPath("$.data.author").value("size must be between 1 and 50"))
                .andExpect(jsonPath("$.data.pages").value("must be greater than or equal to 0"))
                .andExpect(jsonPath("$.data.releaseDate").value("must be a date in the past or in the present"));

            verify(bookService, never()).createBook(any(BookCreateRequest.class));
        }

    }

}
