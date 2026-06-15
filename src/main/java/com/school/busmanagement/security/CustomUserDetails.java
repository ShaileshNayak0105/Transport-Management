package com.school.busmanagement.security;

import com.school.busmanagement.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Spring Security expects a collection of authorities. We convert our Role
    // enum into the ROLE_ADMIN / ROLE_PARENT format used by hasRole checks.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Email acts as the username in this application.
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Helper method for application code that needs the logged-in user's database ID.
    public Long getId() {
        return user.getId();
    }

    // Helper method for screens that want the user's display name.
    public String getName() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
