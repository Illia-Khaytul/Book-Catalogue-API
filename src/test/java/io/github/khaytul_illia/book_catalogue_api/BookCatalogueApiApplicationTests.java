package io.github.khaytul_illia.book_catalogue_api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@DisplayName("Application loading tests")
class BookCatalogueApiApplicationTests {

    @Autowired
    private PostgreSQLContainer postgres;

    @Test
    @DisplayName("Should load application context and start a PostgreSQL testcontainer")
    void shouldLoadContextAndTestcontainer() {
        //Assert
        assertTrue(postgres.isRunning());
    }

}
