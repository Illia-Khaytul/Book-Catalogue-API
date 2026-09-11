package io.github.khaytul_illia.book_catalogue_api.book;

import io.github.khaytul_illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul_illia.book_catalogue_api.common.pagination.PaginatedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public BookResponse createBook(BookCreateRequest request) {
        return null;
    }

    public BookResponse updateBook(long bookId, BookUpdateRequest request) {
        return null;
    }

    public BookResponse getBook(long bookId) {
        return null;
    }

    public PaginatedResponse<BookResponse> getBooks(BookFiltering filtering, Pageable pagination) {
        return null;
    }

    public BookResponse deleteBook(long bookId) {
        return null;
    }

}
