package io.github.khaytul.illia.book_catalogue_api;

import org.springframework.boot.SpringApplication;

public class TestBookCatalogueApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(BookCatalogueApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
