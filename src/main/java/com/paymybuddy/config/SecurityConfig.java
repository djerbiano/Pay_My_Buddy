package com.paymybuddy.config;

import com.paymybuddy.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Expose l'AuthenticationManager comme Bean Spring.
     * Permet à Spring Security d'utiliser CustomUserDetailsService
     * branché sur la base de données, au lieu du système par défaut en mémoire.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Définit BCrypt comme algorithme de hashage des mots de passe.
     * BCrypt est adapté au stockage de mots de passe car il est
     * intentionnellement lent et résistant aux attaques par force brute.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure les règles de sécurité HTTP :
     * - Pages publiques : /login, /register, /css/**, /.well-known/**
     * - Toutes les autres pages nécessitent une authentification
     * - Formulaire de login personnalisé avec redirection vers /transfer
     * - Déconnexion avec redirection vers /login
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/.well-known/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/transfer",true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll())
        ;
        return http.build();
    }
}
