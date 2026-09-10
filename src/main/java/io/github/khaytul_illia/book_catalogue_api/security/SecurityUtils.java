package io.github.khaytul_illia.book_catalogue_api.security;

import io.github.khaytul_illia.book_catalogue_api.exception.EntityNotFoundException;
import io.github.khaytul_illia.book_catalogue_api.exception.UserNotAuthenticatedException;
import io.github.khaytul_illia.book_catalogue_api.user.User;
import io.github.khaytul_illia.book_catalogue_api.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loadAuthenticatedUser() {
        AppUserDetails details = getAuthenticatedUserDetails();

        return userRepository.findById(details.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("Authenticated user does not exist"));
    }

    public AppUserDetails getAuthenticatedUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }

        Object userDetails = auth.getPrincipal();
        if (!(userDetails instanceof AppUserDetails)) {
            throw new UserNotAuthenticatedException("Invalid principal");
        }

        return (AppUserDetails) userDetails;
    }

}
