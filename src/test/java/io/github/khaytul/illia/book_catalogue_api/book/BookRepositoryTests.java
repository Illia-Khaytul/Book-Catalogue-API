package io.github.khaytul.illia.book_catalogue_api.book;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import io.github.khaytul.illia.book_catalogue_api.config.SliceTestcontainersConfig;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("BookRepository tests")
public class BookRepositoryTests extends SliceTestcontainersConfig{

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("existsByTitleAndAuthor tests")
    class ExistsByTitleAndAuthorTests{

        @Test
        @DisplayName("Should return true when book exists by title and author")
        void shouldReturnTrue_whenExists(){
            //Arrange
            Book book = new Book();
            book.setTitle("Cool Book");
            book.setAuthor("Original Author");
            entityManager.persistAndFlush(book);

            //Act
            boolean exists = bookRepository.existsByTitleAndAuthor(book.getTitle(), book.getAuthor());

            //Assert
            assertThat(exists).isTrue();
        }
        
        @Test
        @DisplayName("Should return false when book's author exists but the title not")
        void shouldReturnFalse_whenAuthorExistsButTitleNot(){
            //Arrange
            Book book = new Book();
            book.setTitle("Cool Book");
            book.setAuthor("Original Author");
            entityManager.persistAndFlush(book);

            //Act
            boolean exists = bookRepository.existsByTitleAndAuthor("not exists", book.getAuthor());

            //Assert
            assertThat(exists).isFalse();
        }
        
        @Test
        @DisplayName("Should return false when book does not exist by title and author")
        void shouldReturnFalse_whenNotExists(){
            //Act
            boolean exists = bookRepository.existsByTitleAndAuthor("not exists", "not exists");

            //Assert
            assertThat(exists).isFalse();
        }
        
    }
    
    @Nested
    @DisplayName("deleteBookDirectly tests")
    class DeleteBookDirectlyTests{
        
        @Test
        @DisplayName("Should delete book by id")
        void shouldDeleteBookById(){
            //Arrange
            Book book = new Book();
            book.setTitle("Cool Book");
            book.setAuthor("Original Author");
            book = entityManager.persistAndFlush(book);

            //Act
            bookRepository.deleteBookDirectly(book.getId());
            entityManager.clear();

            //Assert
            assertThat(entityManager.find(Book.class, book.getId())).isNull();
        }

    }
    
}
