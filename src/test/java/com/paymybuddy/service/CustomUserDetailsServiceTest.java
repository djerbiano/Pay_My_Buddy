package com.paymybuddy.service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    /**
     * Initialise les données de test réutilisées dans tous les tests.
     * Appelé automatiquement avant chaque méthode de test.
     */
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("Saber");
        user.setEmail("saber@mail.com");
        user.setPassword("hashedPassword");
        user.setBalance(BigDecimal.valueOf(100));
        user.setConnections(new ArrayList<>());
    }

    /**
     * Vérifie que loadUserByUsername retourne un UserDetails valide
     * quand l'utilisateur existe en base.
     */
    @Test
    void loadUserByUsername_devraitRetournerUserDetails_quandUserExiste() {
        // Arrange
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(user);

        // Act
        UserDetails result = customUserDetailsService.loadUserByUsername("saber@mail.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("saber@mail.com");
        assertThat(result.getPassword()).isEqualTo("hashedPassword");
    }

    /**
     * Vérifie qu'une UsernameNotFoundException est levée
     * quand l'utilisateur n'existe pas en base.
     */
    @Test
    void loadUserByUsername_devraitLancerException_quandUserIntrouvable() {
        // Arrange
        when(userRepository.findByEmail("inconnu@mail.com")).thenReturn(null);

        // Act + Assert
        assertThatThrownBy(() ->
                customUserDetailsService.loadUserByUsername("inconnu@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}