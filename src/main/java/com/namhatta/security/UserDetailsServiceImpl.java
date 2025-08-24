package com.namhatta.security;

import com.namhatta.entity.User;
import com.namhatta.entity.UserRole;
import com.namhatta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);
        
        User user = userRepository.findByUsernameAndIsActive(username, true)
            .orElseThrow(() -> {
                log.warn("User not found or not active: {}", username);
                return new UsernameNotFoundException("User not found: " + username);
            });
        
        log.debug("Found active user: {} with role: {}", username, user.getRole());
        return new UserPrincipal(user);
    }
    
    /**
     * Custom UserDetails implementation
     */
    public static class UserPrincipal implements UserDetails {
        private final User user;
        
        public UserPrincipal(User user) {
            this.user = user;
        }
        
        public User getUser() {
            return user;
        }
        
        public Long getId() {
            return user.getId();
        }
        
        public UserRole getRole() {
            return user.getRole();
        }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // Map user role to Spring Security authorities
            String roleName = "ROLE_" + user.getRole().name();
            return Collections.singletonList(new SimpleGrantedAuthority(roleName));
        }
        
        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }
        
        @Override
        public String getUsername() {
            return user.getUsername();
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
            return user.getIsActive();
        }
        
        @Override
        public String toString() {
            return "UserPrincipal{" +
                    "id=" + user.getId() +
                    ", username='" + user.getUsername() + '\'' +
                    ", role=" + user.getRole() +
                    ", active=" + user.getIsActive() +
                    '}';
        }
    }
}