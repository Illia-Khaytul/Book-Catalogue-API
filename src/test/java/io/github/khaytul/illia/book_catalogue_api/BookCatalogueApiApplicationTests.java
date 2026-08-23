package io.github.khaytul.illia.book_catalogue_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import io.github.khaytul.illia.book_catalogue_api.config.TestcontainersConfig;

@Import(TestcontainersConfig.class)
@SpringBootTest
class BookCatalogueApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
