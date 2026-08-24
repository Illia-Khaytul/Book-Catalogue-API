package io.github.khaytul.illia.book_catalogue_api.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.khaytul.illia.book_catalogue_api.security.exception.AuthenticationErrorHandler;
import io.github.khaytul.illia.book_catalogue_api.user.UserController;
import io.github.khaytul.illia.book_catalogue_api.user.UserService;
import io.github.khaytul.illia.book_catalogue_api.user.request.PasswordChangeRequest;
import io.github.khaytul.illia.book_catalogue_api.user.request.UserCreateRequest;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, AuthenticationErrorHandler.class})
@ActiveProfiles("test")
@DisplayName("User endpoint security tests")
public class UserEndpointSecurityTests {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AppUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("createUser security tests")
    class CreateUserSecurityTests{

        private final UserCreateRequest request = new UserCreateRequest(
            "username", 
            "password"
        );

        @BeforeEach
        public void beforeEach(){
            //Arrange
            doNothing().when(userService)
                .createUser(request);
        }

        @Test
        @DisplayName("Should return 201 Created when accessed with no authentication")
        public void shouldReturn201_whenNoAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated());
        }
        
        @Test
        @WithMockUser
        @DisplayName("Should return 201 Created when accessed with authentication")
        public void shouldReturn201_whenWithAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated());
        }

    }
    
    @Nested
    @DisplayName("changePassword security tests")
    class ChangePasswordSecurityTests{

        private final PasswordChangeRequest request = new PasswordChangeRequest(
            "oldPassword", 
            "newPassword"
        );

        @BeforeEach
        public void beforeEach(){
            //Arrange
            doNothing().when(userService)
                .changePassword(any(PasswordChangeRequest.class));
        }
        
        @Test
        @DisplayName("Should return 401 Unauthorized when accessed with no authentication")
        public void shouldReturn401_whenNoAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                patch("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized());
        }
        
        @Test
        @WithMockUser
        @DisplayName("Should return 200 Ok when accessed with authentication")
        public void shouldReturn200_whenWithAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                patch("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());
        }
    }
    
    @Nested
    @DisplayName("deleteUser security tests")
    class DeleteUserSecurityTests{
        
        @BeforeEach
        public void beforeEach(){
            //Arrange
            doNothing().when(userService)
                .deleteUser();
        }
        
        @Test
        @DisplayName("Should return 401 Unauthorized when accessed with no authentication")
        public void shouldReturn401_whenNoAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                delete("/users")
            )
            .andExpect(status().isUnauthorized());
        }
        
        @Test
        @WithMockUser
        @DisplayName("Should return 204 No Content when accessed with authentication")
        public void shouldReturn204_whenWithAuthentication() throws Exception{
            //Act and Assert
            mockMvc.perform(
                delete("/users")
            )
            .andExpect(status().isNoContent());
        }

    }
    
}
