package io.github.khaytul.illia.book_catalogue_api.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class SliceTestcontainersConfig {

    @SuppressWarnings("resource")
	@Container
	@ServiceConnection
	static PostgreSQLContainer postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
		.withReuse(true);

	static{
		postgresContainer.start();
	}
    
}
