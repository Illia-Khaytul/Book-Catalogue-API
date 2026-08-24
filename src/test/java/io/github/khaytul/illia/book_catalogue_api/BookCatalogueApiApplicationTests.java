package io.github.khaytul.illia.book_catalogue_api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;

import io.github.khaytul.illia.book_catalogue_api.config.TestcontainersConfig;

@SpringBootTest
@Import(TestcontainersConfig.class)
@ActiveProfiles("test")
@DisplayName("Application loading tests")
class BookCatalogueApiApplicationTests {

	@Autowired
	PostgreSQLContainer postgreSQLContainer;

	@Test
	@DisplayName("Should load application context")
	void shouldLoadContext() {
		//Assert
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

}
