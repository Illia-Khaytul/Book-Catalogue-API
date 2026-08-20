package io.github.khaytul.illia.book_catalogue_api.page;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponseDTO<T extends Object>(
    int page,
    int totalPages,
    int pageSize,
    long totalElements,
    List<T> content
) {

    public PageResponseDTO(Page<T> page){
        this(page.getNumber(), page.getTotalPages(), page.getSize(), page.getTotalElements(), page.getContent());
    }
    
}
