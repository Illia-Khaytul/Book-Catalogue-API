package io.github.khaytul.illia.book_catalogue_api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.github.khaytul.illia.book_catalogue_api.user.User;
import io.github.khaytul.illia.book_catalogue_api.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppUserDetailsService tests")
public class AppUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AppUserDetailsService userDetailsService;

    private final String username = "username";

    @Test
    @DisplayName("Should throw UsernameNotFoundException when username does not exist")
    public void shouldThrowUsernameNotFoundException_whenUsernameNotFound(){
        //Arrange
        when(userRepository.findByUsername(username))
            .thenReturn(Optional.empty());

        //Act and Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("User with username '%s' does not exist", username);
            
        verify(userRepository).findByUsername(username);
    }
        
    @Test
    @DisplayName("Should return UserDetails when username exists")
    public void shouldReturnUserDetails_whenUsernameExists(){
        //Arrange
        User foundUser = new User(1L, username, "password");

        when(userRepository.findByUsername(username))
            .thenReturn(Optional.of(foundUser));

        //Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(AppUserDetails.class);
        AppUserDetails appUser = (AppUserDetails) userDetails;
        assertThat(appUser.getUserId()).isEqualTo(foundUser.getId());
        assertThat(appUser.getUsername()).isEqualTo(foundUser.getUsername());
        assertThat(appUser.getPassword()).isEqualTo(foundUser.getPassword());
        assertThat(appUser.getAuthorities()).hasSize(1);
            
        verify(userRepository).findByUsername(username);
    }
    
}
