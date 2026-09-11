package io.github.khaytul_illia.book_catalogue_api.book;

import io.github.khaytul_illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul_illia.book_catalogue_api.common.pagination.PaginatedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(path = "/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @PostMapping(path = "")
    public ResponseEntity<BookResponse> createBook(
        @Validated @RequestBody BookCreateRequest request
    ){
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
    public BookResponse updateBook(
        @Valid @Positive @PathVariable long bookId,
        @Validated @RequestBody BookUpdateRequest request
    ){
        return bookService.updateBook(bookId, request);
    }

    @GetMapping(path = "/{bookId}")
    public BookResponse getBook(
        @PathVariable long bookId
    ){
        return null;
    }

    @GetMapping(path = "")
    public PaginatedResponse<BookResponse> getBooks(
        Pageable pagination,
        @ModelAttribute BookFiltering filtering
    ){
        return null;
    }

    @DeleteMapping(path = "/{bookId}")
    public void deleteBook(
        @PathVariable long bookId
    ){

    }

}
