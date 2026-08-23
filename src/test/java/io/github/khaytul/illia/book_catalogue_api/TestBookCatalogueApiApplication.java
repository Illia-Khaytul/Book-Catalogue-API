package io.github.khaytul.illia.book_catalogue_api;

import org.springframework.boot.SpringApplication;

import io.github.khaytul.illia.book_catalogue_api.config.TestcontainersConfig;

public class TestBookCatalogueApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(BookCatalogueApiApplication::main).with(TestcontainersConfig.class).run(args);
	}

}
