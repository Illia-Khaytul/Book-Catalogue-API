package io.github.khaytul_illia.book_catalogue_api.common.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginatedResponse<T>(
    int page,
    int totalPages,
    int pageSize,
    long totalElements,
    List<T> content
) {

    public PaginatedResponse(Page<T> page){
        this(page.getNumber(), page.getTotalPages(), page.getSize(), page.getTotalElements(), page.getContent());
    }

}
