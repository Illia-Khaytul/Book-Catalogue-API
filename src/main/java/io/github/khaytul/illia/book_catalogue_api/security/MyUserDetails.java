package io.github.khaytul.illia.book_catalogue_api.security;

import java.util.Collection;
import java.util.Collections;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.github.khaytul.illia.book_catalogue_api.user.User;
import lombok.Setter;

@Setter
public class MyUserDetails implements UserDetails{

    private Long userId;
    private String username;
    private String password;

    public MyUserDetails(User user){
        userId = user.getId();
        username = user.getUsername();
        password = user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }
    
}
