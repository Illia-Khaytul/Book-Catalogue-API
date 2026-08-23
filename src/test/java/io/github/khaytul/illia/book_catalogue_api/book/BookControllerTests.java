package io.github.khaytul.illia.book_catalogue_api.book;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul.illia.book_catalogue_api.common.pagination.PaginatedResponse;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
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
        public void whenValidRequest_shouldReturn201() throws Exception{
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
            .andExpect(header().string("Location", "/api/v1/books/" + response.id()))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));

            verify(bookService).createBook(any(BookCreateRequest.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when required request fields are missing")
        public void whenRequestRequiredFieldsMissing_shouldReturn400() throws Exception{
            //Arrange
            BookCreateRequest request = new BookCreateRequest(null, null, null, null, null);
            
            //Act and Assert
            mockMvc.perform(
                post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.title").value("must not be null"))
            .andExpect(jsonPath("$.data.author").value("must not be null"));

            verify(bookService, never()).createBook(any(BookCreateRequest.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when request fields are invalid")
        public void whenRequestInvalid_shouldReturn400() throws Exception{
            //Arrange
            BookCreateRequest request = new BookCreateRequest(
                "", 
                null, 
                "", 
                -1, 
                LocalDate.parse("2050-01-01")
            );

            //Act and Assert
            mockMvc.perform(
                post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.title").value("size must be between 1 and 100"))
            .andExpect(jsonPath("$.data.author").value("size must be between 1 and 50"))
            .andExpect(jsonPath("$.data.pages").value("must be greater than or equal to 0"))
            .andExpect(jsonPath("$.data.releaseDate").value("must be a date in the past or in the present"));

            verify(bookService, never()).createBook(any(BookCreateRequest.class));
        }

    }
    
    @Nested
    @DisplayName("updateBook tests")
    class UpdateBookTests{
        
        @Test
        @DisplayName("Should return 200 Ok when request is valid")
        public void whenValidRequest_shouldReturn200() throws Exception{
            //Arrange
            long bookId = 1;
            BookUpdateRequest request = new BookUpdateRequest(
                "Cool Book Vol.1", 
                null, 
                "Not An Author", 
                null, 
                null
            );
            BookResponse response = new BookResponse(
                1L,
                "Cool Book Vol.1", 
                "Lorem ipsum dolor sit amet", 
                "Not An Author", 
                200, 
                LocalDate.parse("2020-08-10")
            );

            when(bookService.updateBook(anyLong(), any(BookUpdateRequest.class)))
                .thenReturn(response);

            //Act and Assert
            mockMvc.perform(
                patch("/books/{bookId}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));

            verify(bookService).updateBook(anyLong(), any(BookUpdateRequest.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when path variable is invalid")
        public void whenPathVariableInvalid_shouldReturn400() throws Exception{
            //Arrange
            long bookId = -1;
            BookUpdateRequest request = new BookUpdateRequest(null, null, null, null, null);

            //Act and Assert
            mockMvc.perform(
                patch("/books/{bookId}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.bookId").value("must be greater than 0"));

            verify(bookService, never()).updateBook(anyLong(), any(BookUpdateRequest.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when request fields are invalid")
        public void whenRequestInvalid_shouldReturn400() throws Exception{
            //Arrange
            long bookId = 1;
            BookUpdateRequest request = new BookUpdateRequest(
                "", 
                null, 
                "", 
                -1, 
                LocalDate.parse("2050-01-01")
            );

            //Act and Assert
            mockMvc.perform(
                patch("/books/{bookId}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.title").value("size must be between 1 and 100"))
            .andExpect(jsonPath("$.data.author").value("size must be between 1 and 50"))
            .andExpect(jsonPath("$.data.pages").value("must be greater than or equal to 0"))
            .andExpect(jsonPath("$.data.releaseDate").value("must be a date in the past or in the present"));

            verify(bookService, never()).updateBook(anyLong(), any(BookUpdateRequest.class));
        }

    }
    
    @Nested
    @DisplayName("getBook tests")
    class GetBookTests{
        
        @Test
        @DisplayName("Should return 200 Ok when request is valid")
        public void whenValidRequest_shouldReturn200() throws Exception{
            //Arrange
            long bookId = 1;
            BookResponse response = new BookResponse(
                1L,
                "Cool Book Vol.1", 
                "Lorem ipsum dolor sit amet", 
                "Not An Author", 
                200, 
                LocalDate.parse("2020-08-10")
            );

            when(bookService.getBook(anyLong()))
                .thenReturn(response);

            //Act and Assert
            mockMvc.perform(
                get("/books/{bookId}", bookId)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));

            verify(bookService).getBook(anyLong());
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when path variable is invalid")
        public void whenPathVariableInvalid_shouldReturn400() throws Exception{
            //Arrange
            long bookId = -1;

            //Act and Assert
            mockMvc.perform(
                get("/books/{bookId}", bookId)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.bookId").value("must be greater than 0"));

            verify(bookService, never()).getBook(anyLong());
        }

    }
    
    @Nested
    @DisplayName("getBooks tests")
    class GetBooksTests{
        
        @Test
        @DisplayName("Should return 200 Ok when request is valid")
        public void whenValidRequest_shouldReturn200() throws Exception{
            //Arrange
            BookResponse book = new BookResponse(
                1L,
                "Cool Book Vol.1", 
                "Lorem ipsum dolor sit amet", 
                "Not An Author", 
                200, 
                LocalDate.parse("2020-08-10")
            );
            PaginatedResponse<BookResponse> response = new PaginatedResponse<>(new PageImpl<>(List.of(book)));

            when(bookService.getBooks(any(Pageable.class), any(BookFiltering.class)))
                .thenReturn(response);

            //Act and Assert
            mockMvc.perform(
                get("/books")
                .queryParams(MultiValueMap.fromSingleValue(Map.of(
                    "titleContains", "Book"
                )))
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));

            verify(bookService).getBooks(any(Pageable.class), any(BookFiltering.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when query parameters are invalid")
        public void whenQueryParametersInvalid_shouldReturn400() throws Exception{
            //Act and Assert
            mockMvc.perform(
                get("/books")
                .queryParams(MultiValueMap.fromSingleValue(Map.of(
                    "titleContains", "",
                    "authorName", "",
                    "minPages", "-1",
                    "maxPages", "-1",
                    "releasedAfter", "2050-01-01"
                )))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.titleContains").value("size must be between 1 and 100"))
            .andExpect(jsonPath("$.data.authorName").value("size must be between 1 and 50"))
            .andExpect(jsonPath("$.data.minPages").value("must be greater than or equal to 0"))
            .andExpect(jsonPath("$.data.maxPages").value("must be greater than or equal to 0"))
            .andExpect(jsonPath("$.data.releasedAfter").value("must be a date in the past or in the present"));

            verify(bookService, never()).getBooks(any(Pageable.class), any(BookFiltering.class));
        }
    }
    
    @Nested
    @DisplayName("deleteBook tests")
    class DeleteBookTests{
        
        @Test
        @DisplayName("Should return 204 No Content when request is valid")
        public void whenValidRequest_shouldReturn204() throws Exception{
            //Arrange
            long bookId = 1;

            doNothing().when(bookService)
                .deleteBook(anyLong());

            //Act and Assert
            mockMvc.perform(
                delete("/books/{bookId}", bookId)
            )
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));

            verify(bookService).deleteBook(anyLong());
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when path variable is invalid")
        public void whenPathVariableInvalid_shouldReturn400() throws Exception{
            //Arrange
            long bookId = -1;

            //Act and Assert
            mockMvc.perform(
                delete("/books/{bookId}", bookId)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.bookId").value("must be greater than 0"));

            verify(bookService, never()).deleteBook(anyLong());
        }
        
    }

}
