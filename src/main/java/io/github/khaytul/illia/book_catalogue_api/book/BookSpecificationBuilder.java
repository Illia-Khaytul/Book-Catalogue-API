package io.github.khaytul.illia.book_catalogue_api.book;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import io.github.khaytul.illia.book_catalogue_api.book.request.BookFiltering;

@Component
public class BookSpecificationBuilder {

    public Specification<Book> fromFilter(BookFiltering filtering){
        if(filtering.isEmpty()){
            return Specification.allOf(List.of());
        }

        List<Specification<Book>> filters = new ArrayList<>();
        filters.add(titleContains(filtering.titleContains()));
        filters.add(withAuthorName(filtering.authorName()));
        filters.add(withMinPages(filtering.minPages()));
        filters.add(withMaxPages(filtering.maxPages()));
        filters.add(releasedAfter(filtering.releasedAfter()));
        filters.add(releasedBefore(filtering.releasedBefore()));

        filters = filters.stream().filter(specification -> specification != null).toList();
        
        return Specification.allOf(filters);
    }

    public Specification<Book> titleContains(String titleContains){
        if(titleContains == null || titleContains.isEmpty()){
            return null;
        }
        
        return (root, cq, cb) -> cb.like(cb.lower(root.get("title")), "%" + titleContains.toLowerCase() + "%");
    }

    public Specification<Book> withAuthorName(String authorName){
        if(authorName == null || authorName.isEmpty()){
            return null;
        }
        
        return (root, cq, cb) -> cb.like(cb.lower(root.get("author")), "%" + authorName.toLowerCase() + "%");
    }

    public Specification<Book> withMinPages(Integer minPages){
        if(minPages == null){
            return null;
        }
        
        return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("pages"), minPages);
    }

    public Specification<Book> withMaxPages(Integer maxPages){
        if(maxPages == null){
            return null;
        }
        
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("pages"), maxPages);
    }
    
    public Specification<Book> releasedAfter(LocalDate releasedAfter){
        if(releasedAfter == null){
            return null;
        }
        
        return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("releaseDate"), releasedAfter);
    }

    public Specification<Book> releasedBefore(LocalDate releasedBefore){
        if(releasedBefore == null){
            return null;
        }
        
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("releaseDate"), releasedBefore);
    }
    
}
