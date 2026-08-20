package io.github.khaytul.illia.book_catalogue_api.book;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookFilterDTO;
import io.github.khaytul.illia.book_catalogue_api.book.request.BookRequestDTO;
import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponseDTO;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.DuplicateEntryException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.page.PageResponseDTO;
import jakarta.persistence.OptimisticLockException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BookService {

    private BookRepository bookRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public BookResponseDTO registerBook(BookRequestDTO newBookData) {
        log.info("Registering new book with title '{}' by '{}'", newBookData.title(), newBookData.author());

        log.debug("Checking if a book with this title and author already exists");
        if(bookRepository.existsByTitleAndAuthor(newBookData.title(), newBookData.author())){
            throw new DuplicateEntryException("A book with title '%s' by '%s' already exists", newBookData.title(), newBookData.author());
        }

        log.debug("Creating new book with provided data");
        Book book = new Book();
        book.setTitle(newBookData.title());
        book.setDescription(newBookData.description());
        book.setAuthor(newBookData.author());
        book.setPages(newBookData.pages());
        book.setReleaseDate(newBookData.releaseDate());

        log.debug("Persisting new book");
        book = bookRepository.save(book);

        log.info("New book successfully registered");

        return new BookResponseDTO(book);
    }

    @Retryable(includes = OptimisticLockException.class, maxRetries = 3)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public BookResponseDTO updateBook(long bookId, BookRequestDTO newBookData) {
        log.info("Updating book with id {}", bookId);

        log.debug("Fetching book by provided id");
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book with id '%s' does not exist", bookId));
        
        log.debug("Checking if a book with the new title and author already exists");
        String title = newBookData.title() == null ? book.getTitle() : newBookData.title();
        String author = newBookData.author() == null ? book.getAuthor() : newBookData.author();
        if(!(book.getTitle().equals(title) && book.getAuthor().equals(author)) && bookRepository.existsByTitleAndAuthor(title, author)){
            throw new DuplicateEntryException("A book with title '%s' by '%s' already exists", title, author);
        }

        log.debug("Updating book with provided data");
        book.setTitle(title);
        book.setDescription(newBookData.description() == null ? book.getDescription() : newBookData.description());
        book.setAuthor(author);
        book.setPages(newBookData.pages() == null ? book.getPages() : newBookData.pages());
        book.setReleaseDate(newBookData.releaseDate() == null ? book.getReleaseDate() : newBookData.releaseDate());

        log.debug("Persisting updated book");
        book = bookRepository.save(book);

        log.info("Book successfully updated");

        return new BookResponseDTO(book);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public BookResponseDTO getBook(long bookId) {
        log.info("Getting book with id {}", bookId);

        log.debug("Fetching the book by provided id");
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book with id '%s' does not exist", bookId));
        
        log.info("Book found successfully");

        return new BookResponseDTO(book);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PageResponseDTO<BookResponseDTO> getManyBooks(Pageable pagination, BookFilterDTO bookFilterData) {
        log.info("Getting books with provided pagination and filters");

        log.debug("Building filters with provided data");
        List<Specification<Book>> filters = new ArrayList<>();
        if(bookFilterData.titleContains() != null && !bookFilterData.titleContains().isEmpty()){
            filters.add((root, cq, cb) -> cb.like(cb.lower(root.get("title")), "%" + bookFilterData.titleContains().toLowerCase() + "%"));
        }
        if(bookFilterData.authorName() != null && !bookFilterData.authorName().isEmpty()){
            filters.add((root, cq, cb) -> cb.like(cb.lower(root.get("author")), "%" + bookFilterData.authorName().toLowerCase() + "%"));
        }
        if(bookFilterData.minPages() != null){
            filters.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("pages"), bookFilterData.minPages()));
        }
        if(bookFilterData.maxPages() != null){
            filters.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("pages"), bookFilterData.maxPages()));
        }
        if(bookFilterData.releasedAfter() != null){
            filters.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("releaseDate"), bookFilterData.releasedAfter()));
        }
        if(bookFilterData.releasedBefore() != null){
            filters.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("releaseDate"), bookFilterData.releasedBefore()));
        }
        Specification<Book> filter = Specification.allOf(filters);

        log.debug("Fetching a page of books with provided pagination and filters");
        Page<Book> bookPage = bookRepository.findAll(filter, pagination);

        log.info("Successfully found {} books for {} pages", bookPage.getTotalElements(), bookPage.getTotalPages());

        return new PageResponseDTO<>(bookPage.map(BookResponseDTO::new));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBook(long bookId) {
        log.info("Deleting book with id {}", bookId);

        log.debug("Deleting book");
        bookRepository.deleteBookDirectly(bookId);

        log.info("Book deleted successfully");
    }

}
