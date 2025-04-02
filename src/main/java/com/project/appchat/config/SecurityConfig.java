package com.project.appchat.config;

import com.project.appchat.security.JwtAuthenticationEntryPoint;
import com.project.appchat.security.JwtAuthenticationFilter;
import com.project.appchat.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/public/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/ws/**")).permitAll() // Autoriser les connexions WebSocket
                .requestMatchers(new AntPathRequestMatcher("/topic/**")).permitAll() // Autoriser les destinations STOMP
                .requestMatchers(new AntPathRequestMatcher("/app/**")).permitAll() // Autoriser les destinations de l'application
                .requestMatchers(new AntPathRequestMatcher("/user/**")).permitAll() // Autoriser les messages utilisateur
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll() // Autoriser la console H2
                .requestMatchers(new AntPathRequestMatcher("/*.html")).permitAll() // Autoriser les fichiers HTML
                .requestMatchers(new AntPathRequestMatcher("/*.js")).permitAll() // Autoriser les fichiers JavaScript
                .requestMatchers(new AntPathRequestMatcher("/*.css")).permitAll() // Autoriser les fichiers CSS
                .requestMatchers(new AntPathRequestMatcher("/")).permitAll() // Autoriser la page d'accueil
                .requestMatchers(new AntPathRequestMatcher("/test-api.html")).permitAll() // Autoriser spécifiquement test-api.html
                .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll() // Autoriser les ressources statiques
                .requestMatchers(new AntPathRequestMatcher("/public/**")).permitAll() // Autoriser les ressources publiques
                .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
                .anyRequest().authenticated()
            );
            
        // Permettre les frames pour H2 Console
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}