package io.github.khaytul.illia.book_catalogue_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;

@SpringBootApplication
@EnableResilientMethods(order = 100)
public class BookCatalogueApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookCatalogueApiApplication.class, args);
	}

}
