package io.github.khaytul_illia.book_catalogue_api.book;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;

@Entity
@Table(
    name = "books",
    uniqueConstraints = @UniqueConstraint(
        name = "unique_title_per_author",
        columnNames = {"title", "author"}
    )
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @JdbcTypeCode(Types.LONGVARCHAR)
    private String description;

    @Column(nullable = false)
    private String author;

    private Integer pages;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Version
    private Integer version;

}
