package com.namhatta.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.namhatta.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final SessionService sessionService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = getTokenFromRequest(request);
            
            if (token != null && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);
                String sessionToken = tokenProvider.getSessionTokenFromToken(token);
                
                // Validate session (enforces single login like Node.js)
                if (sessionToken != null && sessionService.isSessionValid(username, sessionToken)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        // Add user info to request for controllers
                        request.setAttribute("currentUser", userDetails);
                        request.setAttribute("userId", tokenProvider.getUserIdFromToken(token));
                        request.setAttribute("userRole", tokenProvider.getRoleFromToken(token));
                        
                        log.trace("Successfully authenticated user: {} with role: {}", 
                                 username, tokenProvider.getRoleFromToken(token));
                    }
                } else {
                    log.debug("Invalid session for user: {}", username);
                    SecurityContextHolder.clearContext();
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
            // Clear any existing authentication
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        // Check HTTP-only cookie first (same as Node.js)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    log.trace("Found auth token in cookie");
                    return cookie.getValue();
                }
            }
        }
        
        // Fallback to Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            log.trace("Found auth token in Authorization header");
            return bearerToken.substring(7);
        }
        
        return null;
    }
}