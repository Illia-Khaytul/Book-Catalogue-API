package io.github.khaytul_illia.book_catalogue_api.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    boolean existsByTitleAndAuthor(String title, String author);

    @Transactional
    @Modifying
    @Query("DELETE FROM Book book WHERE book.id = :bookId")
    void deleteBookDirectly(long bookId);

}
