package io.github.khaytul.illia.book_catalogue_api.openapi;

import java.time.Instant;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.khaytul.illia.book_catalogue_api.book.response.BookResponseDTO;
import io.github.khaytul.illia.book_catalogue_api.exception.ExceptionResponseDTO;
import io.github.khaytul.illia.book_catalogue_api.page.PageResponseDTO;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
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
                buildApiResponse("BookResponseDTO", "Operation successful", null)
            )
            .addResponses(
                "page_success_response", 
                buildApiResponse("PageResponseDTO", "Operation successful", null)
            )
            .addResponses(
                "400_response", 
                buildExceptionApiResponse("Invalid parameters", "exception_response_with_data_example")
            )
            .addResponses(
                "401_response", 
                buildExceptionApiResponse("Authentication failed", "exception_response_example")
            )
            .addResponses(
                "404_response", 
                buildExceptionApiResponse("Entity not found", "exception_response_example")
            )
            .addResponses(
                "409_response", 
                buildExceptionApiResponse("Entity parameter conflict", "exception_response_example")
            )
            .addResponses(
                "500_response", 
                buildExceptionApiResponse("Unexpected error", "exception_response_example")
            )
            .addExamples(
                "exception_response_example",
                new Example()
                    .value(new ExceptionResponseDTO(
                        Instant.parse("2026-08-15T03:18:59Z"),
                        0,
                        "string",
                        Map.of()
                    ))
            )
            .addExamples(
                "exception_response_with_data_example",
                new Example()
                    .value(new ExceptionResponseDTO(
                        Instant.parse("2026-08-15T03:18:59Z"),
                        0,
                        "string",
                        Map.of(
                            "data1", "string",
                            "data2", "string",
                            "data3", "string"
                        )
                    ))
            );

        addSchema(components, BookResponseDTO.class);
        addSchema(components, PageResponseDTO.class);
        addSchema(components, ExceptionResponseDTO.class);

        return components;
    }

    private Info assembleInfo(){
        return new Info()
            .title("Book Catalogue API")
            .version("v1")
            .summary("A simple api for managing books and basic user authentication")
            .description("This api provides user registration, pasword change and deletion endpoints, " + 
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
    
    private ApiResponse buildApiResponse(String schemaName, String description, String exampleName){
        MediaType mediaType = new MediaType()
            .schema(new Schema<>().$ref("#/components/schemas/" + schemaName));
        if(exampleName != null && !exampleName.isBlank()){
            mediaType.addExamples(
                "default",
                new Example().$ref("#/components/examples/" + exampleName)
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

    private ApiResponse buildExceptionApiResponse(String description, String exampleName){
        return buildApiResponse("ExceptionResponseDTO", description, exampleName);
    }
    
}
