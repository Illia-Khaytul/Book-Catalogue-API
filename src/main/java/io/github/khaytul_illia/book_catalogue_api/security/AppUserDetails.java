package io.github.khaytul_illia.book_catalogue_api.security;

import io.github.khaytul_illia.book_catalogue_api.user.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUserDetails implements UserDetails {

    private String username;
    private String password;
    private Long userId;

    public AppUserDetails(User user){
        username = user.getUsername();
        password = user.getPassword();
        userId = user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getUserId(){
        return userId;
    }

}
