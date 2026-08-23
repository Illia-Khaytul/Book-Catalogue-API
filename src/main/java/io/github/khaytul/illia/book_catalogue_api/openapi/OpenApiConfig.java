package io.github.khaytul.illia.book_catalogue_api.openapi;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponse;
import io.github.khaytul.illia.book_catalogue_api.exception.ErrorResponse;
import io.github.khaytul.illia.book_catalogue_api.common.pagination.PaginatedResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@Profile("dev")
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(){
        return new OpenAPI()
            .info(assembleInfo())
            .components(assembleComponents());
    }

    private Components assembleComponents(){
        Components components = new Components()
            .addSecuritySchemes(
            "basicAuth", 
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")
            )
            .addResponses(
                "book_success_response", 
                buildApiResponse("BookResponse", "Operation successful", new BookResponse(1L, "Good Book Vol.1", "Lorem ipsum dolor sit amet", 
                    "Original Author", 200, LocalDate.parse("2026-08-15")))
            )
            .addResponses(
                "book_created_response", 
                buildApiResponse("BookResponse", "Operation successful", new BookResponse(1L, "Good Book Vol.1", "Lorem ipsum dolor sit amet", 
                    "Original Author", 200, LocalDate.parse("2026-08-15"))).headers(Map.of("Location", new Header().schema(new StringSchema().example("/api/v1/books/1"))))
            )
            .addResponses(
                "book_page_response", 
                buildApiResponse("PaginatedResponse", "Operation successful", new PaginatedResponse<>(5, 10, 2, 50, List.of(
                    new BookResponse(1L, "Good Book Vol.1", "Lorem ipsum dolor sit amet", "Original Author", 200, LocalDate.parse("2020-08-15")), 
                    new BookResponse(2L, "Good Book Vol.2", "Quisque at arcu quis nisi auctor", "Original Author", 205, LocalDate.parse("2022-08-15")))))
            )
            .addResponses(
                "400_response", 
                buildExceptionApiResponse("Invalid parameters", new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request parameters", 
                    Map.of("title", "Cannot be null nor empty", "pages", "Must be positive or 0")))
            )
            .addResponses(
                "401_response", 
                buildExceptionApiResponse("Authentication failed", new ErrorResponse(HttpStatus.UNAUTHORIZED, "Bad credentials"))
            )
            .addResponses(
                "404_response", 
                buildExceptionApiResponse("Entity not found", new ErrorResponse(HttpStatus.NOT_FOUND, "Book with id 1 does not exist"))
            )
            .addResponses(
                "409_response", 
                buildExceptionApiResponse("Entity parameter conflict", new ErrorResponse(HttpStatus.CONFLICT, "A book with title 'book1' by 'author' already exists"))
            )
            .addResponses(
                "500_response", 
                buildExceptionApiResponse("Unexpected error", new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"))
            );

        addSchema(components, BookResponse.class);
        addSchema(components, PaginatedResponse.class);
        addSchema(components, ErrorResponse.class);

        return components;
    }

    private Info assembleInfo(){
        return new Info()
            .title("Book Catalogue API")
            .version("v1")
            .summary("A simple api for managing books and basic user authentication")
            .description("This api provides user registration, password change and deletion endpoints, " + 
                "as well as standard crud operations for books. It uses basic user authentication to access the " + 
                "endpoints and has exception handling with custom error responses");
    }

    private void addSchema(Components components, Class<?> schemaClass){
        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
            .resolveAsResolvedSchema(new AnnotatedType(schemaClass).resolveAsRef(true));

        if (resolvedSchema.referencedSchemas != null) {
            resolvedSchema.referencedSchemas.forEach(components::addSchemas);
        }
    }
    
    private ApiResponse buildApiResponse(String schemaName, String description, Object example){
        MediaType mediaType = new MediaType()
            .schema(new Schema<>().$ref("#/components/schemas/" + schemaName));
        if(example != null){
            mediaType.addExamples(
                "default",
                new Example().value(example)
            );
        }

        return new ApiResponse()
            .description(description)
            .content(new Content()
                .addMediaType(
                    "application/json",
                    mediaType
                )
            );
    }

    private ApiResponse buildExceptionApiResponse(String description, ErrorResponse example){
        if(example != null){
            example = new ErrorResponse(
                Instant.parse("2026-08-15T03:18:59Z"),
                example.status(),
                example.message(),
                example.data()
            );
        }
        
        return buildApiResponse("ErrorResponse", description, example);
    }
    
}
