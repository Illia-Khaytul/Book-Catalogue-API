package io.github.khaytul_illia.book_catalogue_api.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/dummy")
public class DummyController {

    @PostMapping(path = "")
    public void dummyPost(
        @Validated @RequestBody DummyRequestBody request
    ) {
        dummyOperation();
    }

    @GetMapping(path = "/{id}")
    public void dummyGet(
        @Valid @PathVariable @Positive Long id
    ) {
        dummyOperation();
    }

    public void dummyOperation() {
    }

    public record DummyRequestBody(
        @NotNull
        String value
    ) {
    }

}