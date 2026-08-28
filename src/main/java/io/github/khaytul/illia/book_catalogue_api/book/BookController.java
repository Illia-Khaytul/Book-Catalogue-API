package io.github.khaytul.illia.book_catalogue_api.book;

import java.net.URISyntaxException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul.illia.book_catalogue_api.common.pagination.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/books")
@Tag(name = "Books", description = "Endpoints for the standard crud operations on books.")
@SecurityRequirement(name = "basicAuth")
@Slf4j
@Validated
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @PostMapping(path = "")
    @Operation(summary = "Create new book", description = """
        Creates new book with the provided data, with a unique title per author.
        * Fails with '400 Bad Request' if the provided data is not valid.
        * Fails with '409 Conflict' if a book with the same title and author already exists.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "201", ref = "#/components/responses/book_created_response"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/400_response"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401_response"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/409_response")
    })
    public ResponseEntity<BookResponse> createBook(
        @Validated @RequestBody BookCreateRequest request
    ) throws URISyntaxException{
        BookResponse response = bookService.createBook(request);

        return ResponseEntity
            .created(ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{bookId}")
                .buildAndExpand(response.id())
                .toUri()
            )
            .body(response);
    }
    
    @PatchMapping(path = "/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update book fields", description = """
        Updates existing book with the provided data by id, with a new unique title and author.
        * Fails with '400 Bad Request' if the provided book id is not positive or book data is not valid.
        * Fails with '404 Not Found' if a book with the provided id does not exist.
        * Fails with '409 Conflict' if a book with the new title and author already exists.
        * Fails with '409 Conflict' if the book has been modified concurrently.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", ref = "#/components/responses/book_success_response"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/400_response"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401_response"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/404_response"),
        @ApiResponse(responseCode = "409", ref = "#/components/responses/409_response")
    })
    public BookResponse updateBook(
        @Valid @PathVariable @Positive long bookId,
        @Validated @RequestBody BookUpdateRequest request
    ){
        return bookService.updateBook(bookId, request);
    }
    
    @GetMapping(path = "/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get one book", description = """
        Returns an existing book by id.
        * Fails with '400 Bad Request' if the provided book id is not positive.
        * Fails with '404 Not Found' if a book with the provided id does not exist.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", ref = "#/components/responses/book_success_response"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/400_response"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401_response"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/404_response")
    })
    public BookResponse getBook(
        @Valid @PathVariable @Positive long bookId
    ){
        return bookService.getBook(bookId);
    }
    
    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get multiple books", description = """
        Returns a page of books that match the specified filtering parameters.
        * Accepts both pagination and sorting query parameters.
        * Fails with '400 Bad Request' if the provided filtering data is not valid.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", ref = "#/components/responses/book_page_response"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/400_response"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401_response")
    })
    public PaginatedResponse<BookResponse> getBooks(
        @PageableDefault(page = 0, size = 20, sort = "id", direction = Direction.DESC) Pageable pagination,
        @Validated @ModelAttribute BookFiltering filtering
    ){
        return bookService.getBooks(pagination, filtering);
    }
    
    @DeleteMapping(path = "/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete book", description = """
        Deletes an existing book by id.
        * Fails with '400 Bad Request' if the provided book id is not positive.
        * Fails with '404 Not Found' if a book with the provided id does not exist.
        """)
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Operation successful"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/400_response"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/401_response"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/404_response")
    })
    public void deleteBook(
        @Valid @PathVariable @Positive long bookId
    ){
        bookService.deleteBook(bookId);
    }
    
}
