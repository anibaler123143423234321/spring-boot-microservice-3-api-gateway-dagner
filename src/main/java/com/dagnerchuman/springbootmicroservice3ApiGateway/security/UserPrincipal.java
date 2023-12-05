package com.dagnerchuman.springbootmicroservice3ApiGateway.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.dagnerchuman.springbootmicroservice3ApiGateway.model.User;

import java.util.Collection;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private User user;
    private Set<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    // Constructor que toma un User como parámetro
    public UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.user = user;
        // Puedes inicializar authorities según los roles del usuario
    }

    public static UserPrincipal build(User user) {
        return new UserPrincipal(user);
    }
}
