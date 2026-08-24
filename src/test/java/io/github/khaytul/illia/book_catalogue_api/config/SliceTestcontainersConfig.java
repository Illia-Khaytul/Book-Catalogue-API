package io.github.khaytul.illia.book_catalogue_api.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class SliceTestcontainersConfig {

	@ServiceConnection
	static final PostgreSQLContainer postgresContainer;

	static{
		postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:18-alpine"));
		postgresContainer.start();
	}

    
}
