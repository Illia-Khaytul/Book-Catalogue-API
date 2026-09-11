package io.github.khaytul_illia.book_catalogue_api.security.exception;

import io.github.khaytul_illia.book_catalogue_api.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@Slf4j
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public AuthenticationExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException e
    ) throws IOException, ServletException {
        log.warn("Caught {}: {}", e.getClass().getName(), e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED,
            e.getMessage()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }

}
