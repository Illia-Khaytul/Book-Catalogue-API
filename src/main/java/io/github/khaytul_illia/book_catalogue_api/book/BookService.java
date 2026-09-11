package io.github.khaytul_illia.book_catalogue_api.book;

import io.github.khaytul_illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul_illia.book_catalogue_api.common.pagination.PaginatedResponse;
import io.github.khaytul_illia.book_catalogue_api.exception.DuplicateEntryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public BookResponse createBook(BookCreateRequest request) {
        log.info("Creating new book with title '{}' by '{}'", request.title(), request.author());

        log.debug("Checking if a book with this title and author already exists");
        if(bookRepository.existsByTitleAndAuthor(request.title(), request.author())){
            throw new DuplicateEntryException("A book with title '%s' by '%s' already exists", request.title(), request.author());
        }

        log.debug("Creating new book with provided data");
        Book book = createBookFromRequest(request);

        book = bookRepository.save(book);

        log.info("New book successfully created with id {}", book.getId());

        return new BookResponse(book);
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

    /*
            Helper methods
    */

    private Book createBookFromRequest(BookCreateRequest request){
        Book book = new Book();
        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setAuthor(request.author());
        book.setPages(request.pages());
        book.setReleaseDate(request.releaseDate());

        return book;
    }

}
