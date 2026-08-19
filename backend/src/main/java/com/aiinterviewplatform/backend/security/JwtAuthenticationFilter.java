package com.aiinterviewplatform.backend.security;

import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.repository.UserRepository;
import com.aiinterviewplatform.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("JWT FILTER RUNNING");

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            System.out.println("BEARER TOKEN FOUND");

            String jwt = authHeader.substring(7);

            try {

                String userId = jwtService.extractUserId(jwt);

                System.out.println("USER ID FROM TOKEN: " + userId);

                User user = userRepository.findById(UUID.fromString(userId))
                        .orElse(null);

                System.out.println("USER FOUND: " + (user != null));

                if (user != null && jwtService.isTokenValid(jwt, user)) {

                    System.out.println("JWT VALID");

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    Collections.emptyList()
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println("AUTHENTICATION SET");
                }

            } catch (Exception ex) {

                SecurityContextHolder.clearContext();

                System.out.println("INVALID JWT");
            }
        }
        filterChain.doFilter(request, response);
    }

}