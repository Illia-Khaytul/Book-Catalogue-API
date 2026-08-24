package io.github.khaytul.illia.book_catalogue_api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.UserNotAuthenticatedException;
import io.github.khaytul.illia.book_catalogue_api.user.User;
import io.github.khaytul.illia.book_catalogue_api.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserDetailsService tests")
public class SecurityUtilsTests {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SecurityUtils securityUtils;

    @Nested
    @DisplayName("loadAuthenticatedUser tests")
    class LoadAuthenticatedUserTests{

        private final SecurityUtils securityUtilsSpy = spy(securityUtils);
        private final AppUserDetails userDetails = new AppUserDetails(new User(1L, "username", "password"));

        @Test
        @DisplayName("Should throw EntityNotFoundException when authenticated user does not exist")
        public void shoudlThrowEntityNotFoundException_whenAuthenticatedUserDoesNotExist(){
            //Arrange
            doReturn(userDetails).when(securityUtilsSpy)
                .getAuthenticatedUserDetails();
            when(userRepository.findById(userDetails.getUserId()))
                .thenReturn(Optional.empty());

            //Act and Assert
            assertThatThrownBy(() -> securityUtilsSpy.loadAuthenticatedUser())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Authenticated user does not exist");
            
            verify(securityUtilsSpy).getAuthenticatedUserDetails();
            verify(userRepository).findById(userDetails.getUserId());
        }
        
        @Test
        @DisplayName("Should return loaded user when authenticated user exists")
        public void shoudlReturnLoadedUser_whenAuthenticatedUserExists(){
            //Arrange
            User user = new User(1L, "username", "password");
            
            doReturn(userDetails).when(securityUtilsSpy)
                .getAuthenticatedUserDetails();
            when(userRepository.findById(userDetails.getUserId()))
                .thenReturn(Optional.of(user));

            //Act
            User loadedUser = securityUtilsSpy.loadAuthenticatedUser();

            //Assert
            assertThat(loadedUser).isNotNull();
            assertThat(loadedUser.getId()).isEqualTo(user.getId());
            assertThat(loadedUser.getUsername()).isEqualTo(user.getUsername());
            assertThat(loadedUser.getPassword()).isEqualTo(user.getPassword());
            
            verify(securityUtilsSpy).getAuthenticatedUserDetails();
            verify(userRepository).findById(userDetails.getUserId());
        }

    }
    
    @Nested
    @DisplayName("getAuthenticatedUserDetails tests")
    class GetAuthenticatedUserDetailsTests{

        private static final MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class);
        private final SecurityContext securityContext = mock(SecurityContext.class);
        private final Authentication authentication = mock(Authentication.class);

        @AfterAll
        public static void afterAll(){
            securityContextHolder.close();
        }

        @Test
        @DisplayName("Should throw UserNotAuthenticatedException when authentication is null")
        public void shouldThrowUserNotAuthenticatedException_whenAuthenticationIsNull(){
            //Arrange
            securityContextHolder.when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);
            when(securityContext.getAuthentication())
                .thenReturn(null);

            //Act and Assert
            assertThatThrownBy(() -> securityUtils.getAuthenticatedUserDetails())
                .isInstanceOf(UserNotAuthenticatedException.class)
                .hasMessageContaining("User not authenticated");
            
            securityContextHolder.verify(() -> SecurityContextHolder.getContext());
            verify(securityContext).getAuthentication();
        }

        @Test
        @DisplayName("Should throw UserNotAuthenticatedException when authentication token is not authenticated")
        public void shouldThrowUserNotAuthenticatedException_whenAuthenticationIsNotAuthenticated(){
            //Arrange
            securityContextHolder.when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);
            when(securityContext.getAuthentication())
                .thenReturn(authentication);
            when(authentication.isAuthenticated())
                .thenReturn(false);

            //Act and Assert
            assertThatThrownBy(() -> securityUtils.getAuthenticatedUserDetails())
                .isInstanceOf(UserNotAuthenticatedException.class)
                .hasMessageContaining("User not authenticated");
            
            securityContextHolder.verify(() -> SecurityContextHolder.getContext());
            verify(securityContext).getAuthentication();
            verify(authentication).isAuthenticated();
        }

        @Test
        @DisplayName("Should throw UserNotAuthenticatedException when UserDetails is not instance of AppUserDetails")
        public void shouldThrowUserNotAuthenticatedException_whenUserDetailsNotAppUserDetails(){
            //Arrange
            securityContextHolder.when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);
            when(securityContext.getAuthentication())
                .thenReturn(authentication);
            when(authentication.isAuthenticated())
                .thenReturn(true);
            when(authentication.getPrincipal())
                .thenReturn("Not AppUserDetails");

            //Act and Assert
            assertThatThrownBy(() -> securityUtils.getAuthenticatedUserDetails())
                .isInstanceOf(UserNotAuthenticatedException.class)
                .hasMessageContaining("Invalid principal");
            
            securityContextHolder.verify(() -> SecurityContextHolder.getContext());
            verify(securityContext).getAuthentication();
            verify(authentication).isAuthenticated();
            verify(authentication).getPrincipal();
        }

        @Test
        @DisplayName("Should return AppUserDetails when user is authenticated")
        public void shouldReturnAppUserDetails_whenAuthenticationPresent(){
            //Arrange
            AppUserDetails userDetails = new AppUserDetails(new User(1L, "username", "password"));

            securityContextHolder.when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);
            when(securityContext.getAuthentication())
                .thenReturn(authentication);
            when(authentication.isAuthenticated())
                .thenReturn(true);
            when(authentication.getPrincipal())
                .thenReturn(userDetails);

            //Act
            AppUserDetails returnedUserDetails = securityUtils.getAuthenticatedUserDetails();

            //Assert
            assertThat(returnedUserDetails).isNotNull();
            assertThat(returnedUserDetails.getUserId()).isEqualTo(userDetails.getUserId());
            assertThat(returnedUserDetails.getUsername()).isEqualTo(userDetails.getUsername());
            assertThat(returnedUserDetails.getPassword()).isEqualTo(userDetails.getPassword());
            assertThat(returnedUserDetails.getAuthorities()).hasSize(userDetails.getAuthorities().size());

            securityContextHolder.verify(() -> SecurityContextHolder.getContext());
            verify(securityContext).getAuthentication();
            verify(authentication).isAuthenticated();
            verify(authentication).getPrincipal();
        }

    }
    
}
