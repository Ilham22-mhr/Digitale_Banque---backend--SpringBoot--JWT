package com.bank.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.validateToken(token)) {

                String username = jwtUtil.getUsernameFromToken(token);
                List<String> authorities = jwtUtil.getAuthoritiesFromToken(token);

                System.out.println(" JWT Filter - User: " + username);
                System.out.println(" JWT Filter - Authorities: " + authorities);
                System.out.println("JWT Filter - Endpoint: " + request.getRequestURI());

                List<SimpleGrantedAuthority> grantedAuthorities =
                        authorities.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                grantedAuthorities
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println(" JWT invalide pour la requête: " + request.getRequestURI());
            }
        }

        filterChain.doFilter(request, response);
    }
}