package io.github.khaytul.illia.book_catalogue_api.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.khaytul.illia.book_catalogue_api.user.request.PasswordChangeRequest;
import io.github.khaytul.illia.book_catalogue_api.user.request.UserCreateRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("UserController tests")
public class UserControllerTests {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("createUser tests")
    class CreateUserTests{

        @Test
        @DisplayName("Should return 201 Created when request is valid")
        public void whenValidRequest_shouldReturn201() throws Exception{
            //Arrange
            UserCreateRequest request = new UserCreateRequest(
                "username", 
                "password"
            );

            doNothing().when(userService)
                .createUser(any(UserCreateRequest.class));

            //Act and Assert
            mockMvc.perform(
                post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(content().string(""));

            verify(userService).createUser(any(UserCreateRequest.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when required request fields are missing")
        public void whenRequestRequiredFieldsMissing_shouldReturn400() throws Exception{
            //Arrange
            UserCreateRequest request = new UserCreateRequest(null, null);
            
            //Act and Assert
            mockMvc.perform(
                post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.username").value("must not be null"))
            .andExpect(jsonPath("$.data.password").value("must not be null"));

            verify(userService, never()).createUser(any(UserCreateRequest.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when request fields are invalid")
        public void whenRequestInvalid_shouldReturn400() throws Exception{
            //Arrange
            UserCreateRequest request = new UserCreateRequest("user", "pass");
            
            //Act and Assert
            mockMvc.perform(
                post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.username").value("size must be between 5 and 50"))
            .andExpect(jsonPath("$.data.password").value("size must be between 6 and 50"));

            verify(userService, never()).createUser(any(UserCreateRequest.class));
        }
        
    }

    @Nested
    @DisplayName("changePassword tests")
    class ChangePasswordTests{
        
        @Test
        @DisplayName("Should return 200 OK when request is valid")
        public void shouldReturn200_whenValidRequest() throws Exception{
            //Arrange
            PasswordChangeRequest request = new PasswordChangeRequest(
                "oldPassword", 
                "newPassword"
            );

            doNothing().when(userService)
                .changePassword(any(PasswordChangeRequest.class));

            //Act and Assert
            mockMvc.perform(
                patch("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(content().string(""));

            verify(userService).changePassword(any(PasswordChangeRequest.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when required request fields are missing")
        public void shouldReturn400_whenRequestRequiredFieldsMissing() throws Exception{
            //Arrange
            PasswordChangeRequest request = new PasswordChangeRequest(null, null);
            
            //Act and Assert
            mockMvc.perform(
                patch("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.oldPassword").value("must not be null"))
            .andExpect(jsonPath("$.data.newPassword").value("must not be null"));

            verify(userService, never()).changePassword(any(PasswordChangeRequest.class));
        }
        
        @Test
        @DisplayName("Should return 400 Bad Request when request fields are invalid")
        public void shouldReturn400_whenRequestInvalid() throws Exception{
            //Arrange
            PasswordChangeRequest request = new PasswordChangeRequest("old", "new");
            
            //Act and Assert
            mockMvc.perform(
                patch("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_BAD_REQUEST))
            .andExpect(jsonPath("$.message").isNotEmpty())
            .andExpect(jsonPath("$.data.oldPassword").value("size must be between 6 and 50"))
            .andExpect(jsonPath("$.data.newPassword").value("size must be between 6 and 50"));

            verify(userService, never()).changePassword(any(PasswordChangeRequest.class));
        }

    }

    @Nested
    @DisplayName("deleteUser tests")
    class DeleteUserTests{
        
        @Test
        @DisplayName("Should return 204 No Content")
        public void shouldReturn204() throws Exception{
            //Arrange
            doNothing().when(userService)
                .deleteUser();

            //Act and Assert
            mockMvc.perform(
                delete("/users")
            )
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));

            verify(userService).deleteUser();
        }
        
    }
    
}
