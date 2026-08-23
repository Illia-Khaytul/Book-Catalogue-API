package io.github.khaytul.illia.book_catalogue_api.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
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
    
    private BookFiltering filtering;

    @BeforeEach
    public void beforeEach(){
        filtering = new BookFiltering(
            "title", 
            "author", 
            0, 
            100, 
            LocalDate.parse("2026-12-31"), 
            LocalDate.parse("2000-01-01")
        );
    }

    @Nested
    @DisplayName("fromFilter tests")
    class FromFilterTests{

        @Test
        @DisplayName("Should return empty specification when filtering is empty")
        public void whenFilteringIsEmpty_shouldReturnEmptySpecification(){
            //Arrange
            filtering = new BookFiltering(null, null, null, null, null, null);

            //Act
            Specification<Book> spec = bookSpecificationBuilder.fromFilter(filtering);

            //Assert
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("Should return specification when filtering is not empty")
        public void whenFilteringIsNotEmpty_shouldReturnSpecification(){
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
        public void whenTitleContainsIsNull_shouldReturnNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.titleContains(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return null when titleContains is empty")
        public void whenTitleContainsIsEmpty_shouldReturnNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.titleContains("");

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when titleContains is not null nor empty")
        public void whenTitleContainsIsValid_shouldReturnSpecification(){
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
        public void whenAuthorNameIsNull_shouldReturnNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withAuthorName(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return null when authorName is empty")
        public void whenAuthorNameIsEmpty_shouldReturnNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withAuthorName("");

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when authorName is not null nor empty")
        public void whenAuthorNameIsValid_shouldReturnSpecification(){
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
        public void whenMinPagesIsNull_shouldReturnNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withMinPages(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when minPages is not null")
        public void whenMinPagesIsValid_shouldReturnSpecification(){
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
        public void whenMaxPagesIsNull_shouldReturnNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.withMaxPages(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when maxPages is not null")
        public void whenMaxPagesIsValid_shouldReturnSpecification(){
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
        public void whenReleasedAftersIsNull_shouldReturnNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.releasedAfter(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when releasedAfter is not null")
        public void whenReleasedAfterIsValid_shouldReturnSpecification(){
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
        public void whenReleasedBeforeIsNull_shouldReturnNull(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.releasedBefore(null);

            //Assert
            assertThat(spec).isNull();
        }
        
        @Test
        @DisplayName("Should return specification when maxPages is not null")
        public void whenReleasedBeforeIsValid_shouldReturnSpecification(){
            //Act
            Specification<Book> spec = bookSpecificationBuilder.releasedBefore(filtering.releasedBefore());

            //Assert
            assertThat(spec).isNotNull();
        }

    }
    
}
