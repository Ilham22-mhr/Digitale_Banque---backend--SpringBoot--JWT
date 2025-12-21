package com.bank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final DataSource dataSource;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(DataSource dataSource, JwtAuthenticationFilter jwtFilter) {
        this.dataSource = dataSource;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/clients/**").hasAuthority("CLIENT_READ")
                        .requestMatchers(HttpMethod.POST, "/api/clients").hasAuthority("CLIENT_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/clients/**").hasAuthority("CLIENT_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/clients/**").hasAuthority("CLIENT_DELETE")

                        .requestMatchers(HttpMethod.GET, "/api/comptes/**").hasAuthority("COMPTE_READ")
                        .requestMatchers(HttpMethod.POST, "/api/comptes/**").hasAuthority("COMPTE_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/comptes/*/activer").hasAuthority("COMPTE_ACTIVATE")
                        .requestMatchers(HttpMethod.PUT, "/api/comptes/*/suspendre").hasAuthority("COMPTE_SUSPEND")

                        .requestMatchers("/api/operations/**").hasAuthority("OPERATION_EXECUTE")

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "SELECT username, password, enabled FROM users WHERE username = ?"
                )
                .authoritiesByUsernameQuery(
                        "SELECT u.username, a.name " +
                                "FROM users u " +
                                "JOIN roles r ON u.role_id = r.id " +
                                "JOIN authorities a ON a.role_id = r.id " +
                                "WHERE u.username = ?"
                )
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}