package io.github.khaytul.illia.book_catalogue_api.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>{

    boolean existsByTitleAndAuthor(String title, String author);

    @Modifying
    @Query("DELETE FROM Book book WHERE book.id = :bookId")
    void deleteBookDirectly(long bookId);
    
}
