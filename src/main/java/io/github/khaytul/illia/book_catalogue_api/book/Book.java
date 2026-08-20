package io.github.khaytul.illia.book_catalogue_api.book;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = true)
    @Lob
    private String description;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "pages", nullable = true)
    private Integer pages;

    @Column(name = "release_date", nullable = true)
    private Date releaseDate;

    @Version
    private Integer version;

}
