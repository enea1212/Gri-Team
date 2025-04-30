package com.example.demo.security;

import com.example.demo.service.JwtService;
import com.example.demo.service.UtilizatorService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UtilizatorService utilizatorService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Logăm existența header-ului Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("No Authorization header or no Bearer token found.");
            filterChain.doFilter(request, response); // Continuăm procesarea cererii
            return;
        }

        // Extragem token-ul JWT din header
        jwt = authHeader.substring(7); // elimină "Bearer "
        username = jwtService.extractUsername(jwt);

        // Logăm username-ul extras din token
        logger.info("Extracted username from token: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;

            // Încercăm să obținem detaliile utilizatorului
            try {
                userDetails = utilizatorService.loadUserByUsername(username); // Încărcăm utilizatorul din baza de date
                logger.info("User details found for username: {}", username);
            } catch (UsernameNotFoundException e) {
                logger.error("Username not found in the database: {}", username);
            }

            // Verificăm validitatea token-ului și existența utilizatorului
            if (userDetails != null) {
                logger.info("User details successfully retrieved for username: {}", username);

                if (jwtService.isTokenValid(jwt)) {
                    logger.info("Token is valid for user: {}", username);

                    // Extragem autorizațiile (roluri) din token
                    List<SimpleGrantedAuthority> authorities = jwtService.extractAuthorities(jwt).stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // Creăm token-ul de autentificare
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    // Setăm detaliile autentificării
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Setăm autentificarea în contextul Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Authentication set successfully for user: {}", username);
                } else {
                    logger.warn("Token is invalid for user: {}", username);
                }
            } else {
                logger.warn("User not found or user details are null for username: {}", username);
            }
        } else {
            logger.warn("No username extracted or authentication already exists for this request.");
        }

        // Continuăm cu procesarea cererii
        filterChain.doFilter(request, response);
    }
}
