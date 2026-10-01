package com.turfreservation.security;

import com.turfreservation.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String extractedToken = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            extractedToken = authHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(extractedToken);
            } catch (Exception e) {
                // invalid token, leave email as null
            }
        }

        final String finalToken = extractedToken;

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            userRepository.findByEmail(email).ifPresent(user -> {
                if (jwtUtil.validateToken(finalToken, user.getEmail())) {
                    String role = jwtUtil.extractRole(finalToken);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user, null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            });
        }

        filterChain.doFilter(request, response);
    }
}