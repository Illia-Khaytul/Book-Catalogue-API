package io.github.khaytul.illia.book_catalogue_api.security.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import io.github.khaytul.illia.book_catalogue_api.exception.ExceptionResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
@Slf4j
public class AuthenticationErrorHandler implements AuthenticationEntryPoint{

    private ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException e
    ) throws IOException, ServletException {
        log.warn("{} - {}", e.getClass().getName(), e.getMessage());
        
        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(
            HttpStatus.UNAUTHORIZED, 
            e.getMessage()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
        response.getWriter().flush();
    }
    
}
