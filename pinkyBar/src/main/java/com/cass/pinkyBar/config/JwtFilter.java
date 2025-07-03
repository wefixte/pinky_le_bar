package com.cass.pinkyBar.config;

import com.cass.pinkyBar.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    // Liste des endpoints publics à ignorer par ce filtre
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/auth/login",
            "/auth/register"
            // ajoute ici d'autres chemins publics si besoin
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        logger.info("Request URI: " + path);

        // Si c’est un endpoint public, on skip la vérification du token
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            logger.info("Skipping JWT validation for public endpoint: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        logger.info("Authorization header: " + authHeader);

        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        try {
            email = jwtUtil.extractEmail(token);
            logger.info("JWT extracted email: " + email);
        } catch (Exception e) {
            logger.warn("Invalid JWT token: " + e.getMessage(), e); // affiche stacktrace
        }
    }
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("User authenticated: " + email);
            } else {
                logger.warn("JWT token validation failed for user: " + email);
            }
        } else {
            if (email == null) {
                logger.info("No valid JWT token found in request");
            }
        }

        filterChain.doFilter(request, response);
    }
}
