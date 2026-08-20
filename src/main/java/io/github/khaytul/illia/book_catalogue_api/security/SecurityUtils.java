package io.github.khaytul.illia.book_catalogue_api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.github.khaytul.illia.book_catalogue_api.exception.exceptions.UserNotAuthenticatedException;

public class SecurityUtils {

    public static MyUserDetails getAuthenticatedUserDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()){
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        Object userDetails = auth.getPrincipal();
        if(!(userDetails instanceof MyUserDetails)){
            throw new UserNotAuthenticatedException("Invalid principal");
        }

        return (MyUserDetails) userDetails;
    }

}
