package io.github.khaytul.illia.book_catalogue_api.security.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import io.github.khaytul.illia.book_catalogue_api.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class AuthenticationErrorHandler implements AuthenticationEntryPoint{

    private final ObjectMapper objectMapper;

    public AuthenticationErrorHandler(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
        HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException e
    ) throws IOException, ServletException {
        log.warn("{} - {}", e.getClass().getName(), e.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED, 
            e.getMessage()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
    
}
