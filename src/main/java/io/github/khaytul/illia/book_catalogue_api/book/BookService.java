package io.github.khaytul.illia.book_catalogue_api.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookFiltering;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookCreateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookUpdateRequest;
import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.common.pagination.PaginatedResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    public BookService(BookRepository bookRepository, BookSpecificationBuilder bookSpecificationBuilder){
        this.bookRepository = bookRepository;
        this.bookSpecificationBuilder = bookSpecificationBuilder;
    }

    @Transactional
    public BookResponse createBook(BookCreateRequest request) {
        log.info("Createing new book with title '{}' by '{}'", request.title(), request.author());

        log.debug("Checking if a book with this title and author already exists");
        if(bookRepository.existsByTitleAndAuthor(request.title(), request.author())){
            throw new DuplicateEntryException("A book with title '%s' by '%s' already exists", request.title(), request.author());
        }

        log.debug("Creating new book with provided data");
        Book book = createBookFromRequest(request);

        log.debug("Persisting new book");
        book = bookRepository.save(book);

        log.info("New book successfully createed with id {}", book.getId());

        return new BookResponse(book);
    }

    @Retryable(includes = OptimisticLockException.class, maxRetries = 3)
    @Transactional
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

        log.debug("Persisting updated book");
        book = bookRepository.save(book);

        log.info("Book successfully updated");

        return new BookResponse(book);
    }

    @Transactional(readOnly = true)
    public BookResponse getBook(long bookId) {
        log.info("Getting book with id {}", bookId);

        log.debug("Fetching the book by provided id");
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book with id '%s' does not exist", bookId));
        
        log.info("Book found successfully");

        return new BookResponse(book);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<BookResponse> getBooks(Pageable pagination, BookFiltering filtering) {
        log.info("Getting books with provided pagination and filters");

        log.debug("Building filters with provided data");
        Specification<Book> filter = bookSpecificationBuilder.fromFilter(filtering);

        log.debug("Fetching a page of books with provided pagination and filters");
        Page<Book> bookPage = bookRepository.findAll(filter, pagination);

        log.info("Successfully found {} books for {} pages", bookPage.getTotalElements(), bookPage.getTotalPages());

        return new PaginatedResponse<>(bookPage.map(BookResponse::new));
    }

    @Transactional
    public void deleteBook(long bookId) {
        log.info("Deleting book with id {}", bookId);

        log.debug("Checking if a book with the provided id exists");
        if(!bookRepository.existsById(bookId)){
            throw new EntityNotFoundException("Book with id '%s' does not exist", bookId);
        }

        log.debug("Deleting book");
        bookRepository.deleteBookDirectly(bookId);

        log.info("Book deleted successfully");
    }

    /*
            Helper methods
    */

    public Book createBookFromRequest(BookCreateRequest request){
        Book book = new Book();
        book.setTitle(request.title());
        book.setDescription(request.description());
        book.setAuthor(request.author());
        book.setPages(request.pages());
        book.setReleaseDate(request.releaseDate());

        return book;
    }

    public void updateBookFromRequest(Book book, BookUpdateRequest request){
        book.setTitle(request.title() == null ? book.getTitle() : request.title());
        book.setDescription(request.description() == null ? book.getDescription() : request.description());
        book.setAuthor(request.author() == null ? book.getAuthor() : request.author());
        book.setPages(request.pages() == null ? book.getPages() : request.pages());
        book.setReleaseDate(request.releaseDate() == null ? book.getReleaseDate() : request.releaseDate());
    }

}
