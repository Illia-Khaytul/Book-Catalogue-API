package io.github.khaytul_illia.book_catalogue_api.book;

import io.github.khaytul_illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul_illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul_illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul_illia.book_catalogue_api.common.pagination.PaginatedResponse;
import io.github.khaytul_illia.book_catalogue_api.exception.DuplicateEntryException;
import io.github.khaytul_illia.book_catalogue_api.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.resilience.annotation.Retryable;
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

    @Retryable(includes = OptimisticLockingFailureException.class, maxRetriesString = "${spring.application.retries.update-book.max")
    public BookResponse updateBook(long bookId, BookUpdateRequest request) {
        log.info("Updating book with id {}", bookId);

        log.debug("Fetching book by provided id");
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book with id '%s' does not exist", bookId));

        if(request.isEmpty()){
            log.info("No book fields to update");
            return new BookResponse(book);
        }

        log.debug("Checking if a book with the new title and author already exists");
        String title = request.title() == null ? book.getTitle() : request.title();
        String author = request.author() == null ? book.getAuthor() : request.author();
        if(!(book.getTitle().equals(title) && book.getAuthor().equals(author)) && bookRepository.existsByTitleAndAuthor(title, author)){
            throw new DuplicateEntryException("A book with title '%s' by '%s' already exists", title, author);
        }

        log.debug("Updating book with provided data");
        updateBookFromRequest(book, request);

        book = bookRepository.save(book);

        log.info("Book successfully updated");

        return new BookResponse(book);
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

    private void updateBookFromRequest(Book book, BookUpdateRequest request){
        book.setTitle(request.title() == null ? book.getTitle() : request.title());
        book.setDescription(request.description() == null ? book.getDescription() : request.description());
        book.setAuthor(request.author() == null ? book.getAuthor() : request.author());
        book.setPages(request.pages() == null ? book.getPages() : request.pages());
        book.setReleaseDate(request.releaseDate() == null ? book.getReleaseDate() : request.releaseDate());
    }

}
