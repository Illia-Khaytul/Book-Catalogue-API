package io.github.khaytul.illia.book_catalogue_api.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.khaytul.illia.book_catalogue_api.book.BookController;
import io.github.khaytul.illia.book_catalogue_api.book.BookService;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul.illia.book_catalogue_api.common.pagination.PaginatedResponse;
import io.github.khaytul.illia.book_catalogue_api.security.exception.AuthenticationErrorHandler;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class, AuthenticationErrorHandler.class})
@ActiveProfiles("test")
@DisplayName("Book endpoint security tests")
public class BookEndpointSecurityTests {

    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private AppUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final BookResponse response = new BookResponse(null, null, null, null, null, null);

    @Nested
    @DisplayName("createBook security tests")
    class CreateBookSecurityTests{

        private final BookCreateRequest request = new BookCreateRequest(
            "Cool Book Vol.1", 
            null, 
            "Not An Author", 
            null, 
            null
        );

        @BeforeEach
        public void beforeEach(){
            //Arrange
            when(bookService.createBook(any(BookCreateRequest.class)))
                .thenReturn(response);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when accessed with no authentication")
        public void shouldReturn401_whenNoAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
        }
        
        @Test
        @WithMockUser
        @DisplayName("Should return 201 Created when accessed with authentication")
        public void shouldReturn201_whenWithAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated());
        }

    }
    
    @Nested
    @DisplayName("updateBook security tests")
    class UpdateBookSecurityTests{

        private final BookUpdateRequest request = new BookUpdateRequest(
            "Cool Book Vol.1", 
            null, 
            "Not An Author", 
            null, 
            null
        );            

        @BeforeEach
        public void beforeEach(){
            //Arrange
            when(bookService.updateBook(anyLong(), any(BookUpdateRequest.class)))
                .thenReturn(response);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when accessed with no authentication")
        public void shouldReturn401_whenNoAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                patch("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
        }
        
        @Test
        @WithMockUser
        @DisplayName("Should return 200 Ok when accessed with authentication")
        public void shouldReturn200_whenWithAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                patch("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());
        }

    }
    
    @Nested
    @DisplayName("getBook security tests")
    class GetBookSecurityTests{

        @BeforeEach
        public void beforeEach(){
            //Arrange
            when(bookService.getBook(anyLong()))
                .thenReturn(response);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when accessed with no authentication")
        public void shouldReturn401_whenNoAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                get("/books/1")
            )
            .andExpect(status().isUnauthorized());
        }
        
        @Test
        @WithMockUser
        @DisplayName("Should return 200 Ok when accessed with authentication")
        public void shouldReturn200_whenWithAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                get("/books/1")
            )
            .andExpect(status().isOk());
        }

    }
    
    @Nested
    @DisplayName("getBooks security tests")
    class GetBooksSecurityTests{

        private final PaginatedResponse<BookResponse> response = new PaginatedResponse<>(0, 0, 0, 0L, null);

        @BeforeEach
        public void beforeEach(){
            //Arrange
            when(bookService.getBooks(any(Pageable.class), any(BookFiltering.class)))
                .thenReturn(response);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when accessed with no authentication")
        public void shouldReturn401_whenNoAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                get("/books")
            )
            .andExpect(status().isUnauthorized());
        }
        
        @Test
        @WithMockUser
        @DisplayName("Should return 200 Ok when accessed with authentication")
        public void shouldReturn200_whenWithAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                get("/books")
            )
            .andExpect(status().isOk());
        }

    }
    
    @Nested
    @DisplayName("deleteBook security tests")
    class DeleteBookSecurityTests{

        @BeforeEach
        public void beforeEach(){
            //Arrange
            doNothing().when(bookService)
                .deleteBook(anyLong());
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when accessed with no authentication")
        public void shouldReturn401_whenNoAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                delete("/books/1")
            )
            .andExpect(status().isUnauthorized());
        }
        
        @Test
        @WithMockUser
        @DisplayName("Should return 204 No Content when accessed with authentication")
        public void shouldReturn204_whenWithAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                delete("/books/1")
            )
            .andExpect(status().isNoContent());
        }

    }
    
}
