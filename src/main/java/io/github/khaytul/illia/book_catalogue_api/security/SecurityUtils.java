package io.github.khaytul.illia.book_catalogue_api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.EntityNotFoundException;
import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.UserNotAuthenticatedException;
import io.github.khaytul.illia.book_catalogue_api.user.User;
import io.github.khaytul.illia.book_catalogue_api.user.UserRepository;

@Component
public class SecurityUtils {

    private UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    
    @Transactional
    public User loadAuthenticatedUser(){
        AppUserDetails details = getAuthenticatedUserDetails();
        
        return userRepository.findById(details.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("Authenticated user does not exist"));
    }

    public AppUserDetails getAuthenticatedUserDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()){
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        Object userDetails = auth.getPrincipal();
        if(!(userDetails instanceof AppUserDetails)){
            throw new UserNotAuthenticatedException("Invalid principal");
        }

        return (AppUserDetails) userDetails;
    }
    
}
