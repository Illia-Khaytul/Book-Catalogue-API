package io.github.khaytul.illia.book_catalogue_api.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookFiltering;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookSpecificationBuilder tests")
public class BookSpecificationBuilderTests {

    @InjectMocks
    private BookSpecificationBuilder bookSpecificationBuilder;
    
    private final BookFiltering filtering = new BookFiltering(
        "title", 
        "author", 
        0, 
        100, 
        LocalDate.parse("2026-12-31"), 
        LocalDate.parse("2000-01-01")
    );

    @Nested
    @DisplayName("fromFilter tests")
    class FromFilterTests{

        @Test
        @DisplayName("Should return empty specification when filtering is empty")
        public void shouldReturnEmptySpecification_whenFilteringIsEmpty(){
            //Arrange
            BookFiltering filtering = new BookFiltering(null, null, null, null, null, null);

            //Act
            Specification<Book> spec = bookSpecificationBuilder.fromFilter(filtering);

            //Assert
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("Should return specification when filtering is not empty")
        public void shouldReturnSpecification_whenFilteringIsNotEmpty(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.fromFilter(filtering);

            //Assert
            assertThat(spec).isNotNull();
        }

    }
    
    @Nested
    @DisplayName("titleContains tests")
    class TitleContainsTests{

        @Test
        @DisplayName("Should return null when titleContains is null")
        public void shouldReturnNull_whenTitleContainsIsNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.titleContains(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return null when titleContains is empty")
        public void shouldReturnNull_whenTitleContainsIsEmpty(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.titleContains("");

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when titleContains is not null nor empty")
        public void shouldReturnSpecification_whenTitleContainsIsValid(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.titleContains(filtering.titleContains());

            //Assert
            assertThat(spec).isNotNull();
        }

    }
    
    @Nested
    @DisplayName("withAuthorName tests")
    class WithAuthorNameTests{

        @Test
        @DisplayName("Should return null when authorName is null")
        public void shouldReturnNull_whenAuthorNameIsNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withAuthorName(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return null when authorName is empty")
        public void shouldReturnNull_whenAuthorNameIsEmpty(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withAuthorName("");

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when authorName is not null nor empty")
        public void shouldReturnSpecification_whenAuthorNameIsValid(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withAuthorName(filtering.authorName());

            //Assert
            assertThat(spec).isNotNull();
        }

    }
    
    @Nested
    @DisplayName("withMinPages tests")
    class WithMinPagesTests{

        @Test
        @DisplayName("Should return null when minPages is null")
        public void shouldReturnNull_whenMinPagesIsNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withMinPages(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when minPages is not null")
        public void shouldReturnSpecification_whenMinPagesIsValid(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withMinPages(filtering.minPages());

            //Assert
            assertThat(spec).isNotNull();
        }

    }
    
    @Nested
    @DisplayName("withMaxPages tests")
    class WithMaxPagesTests{

        @Test
        @DisplayName("Should return null when maxPages is null")
        public void shouldReturnNull_whenMaxPagesIsNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withMaxPages(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when maxPages is not null")
        public void shouldReturnSpecification_whenMaxPagesIsValid(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withMaxPages(filtering.maxPages());

            //Assert
            assertThat(spec).isNotNull();
        }

    }
    
    @Nested
    @DisplayName("releasedAfter tests")
    class ReleasedAfterTests{

        @Test
        @DisplayName("Should return null when releasedAfter is null")
        public void shouldReturnNull_whenReleasedAftersIsNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.releasedAfter(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when releasedAfter is not null")
        public void shouldReturnSpecification_whenReleasedAfterIsValid(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.releasedAfter(filtering.releasedAfter());

            //Assert
            assertThat(spec).isNotNull();
        }

    }
    
    @Nested
    @DisplayName("releasedBefore tests")
    class ReleasedBeforeTests{

        @Test
        @DisplayName("Should return null when releasedBefore is null")
        public void shouldReturnNull_whenReleasedBeforeIsNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.releasedBefore(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when maxPages is not null")
        public void shouldReturnSpecification_whenReleasedBeforeIsValid(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.releasedBefore(filtering.releasedBefore());

            //Assert
            assertThat(spec).isNotNull();
        }

    }
    
}
