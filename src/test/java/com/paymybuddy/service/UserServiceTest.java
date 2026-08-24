package com.paymybuddy.service;

import com.paymybuddy.exception.ConnectionAlreadyExistsException;
import com.paymybuddy.exception.EmailAlreadyExistsException;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private User connection;

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

        connection = new User();
        connection.setId(2);
        connection.setUsername("Laure-Anne");
        connection.setEmail("laure@mail.com");
        connection.setPassword("hashedPassword");
        connection.setBalance(BigDecimal.valueOf(50));
        connection.setConnections(new ArrayList<>());
    }

    /**
     * Vérifie qu'un utilisateur est bien créé quand l'email n'existe pas en base.
     * Le mot de passe doit être hashé avant la sauvegarde.
     */
    @Test
    void register_devraitCreerUnUser_quandEmailNexistePas() {
        // Arrange
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.register("Saber", "saber@mail.com", "password123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("Saber");
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Vérifie qu'une EmailAlreadyExistsException est levée si l'email est déjà utilisé.
     * Vérifie aussi que save() n'est jamais appelé.
     */
    @Test
    void register_devraitLancerException_quandEmailDejaUtilise() {
        // Arrange
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(user);

        // Act + Assert
        assertThatThrownBy(() ->
                userService.register("Saber", "saber@mail.com", "password123"))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Vérifie qu'une relation est bien ajoutée quand les données sont valides.
     * La connection doit apparaître dans la liste des connections du user.
     */
    @Test
    void addConnection_devraitAjouterRelation_quandDonneesValides() {
        // Arrange
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(user);
        when(userRepository.findByEmail("laure@mail.com")).thenReturn(connection);

        // Act
        userService.addConnection("saber@mail.com", "laure@mail.com");

        // Assert
        assertThat(user.getConnections()).contains(connection);
        verify(userRepository, times(1)).save(user);
    }

    /**
     * Vérifie qu'une UserNotFoundException est levée
     * quand l'email de la connection n'existe pas en base.
     */
    @Test
    void addConnection_devraitLancerException_quandConnectionIntrouvable() {
        // Arrange
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(user);
        when(userRepository.findByEmail("inconnu@mail.com")).thenReturn(null);

        // Act + Assert
        assertThatThrownBy(() ->
                userService.addConnection("saber@mail.com", "inconnu@mail.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    /**
     * Vérifie qu'une ConnectionAlreadyExistsException est levée
     * quand l'utilisateur tente de s'ajouter lui-même en relation.
     */
    @Test
    void addConnection_devraitLancerException_quandAjoutDeSoiMeme() {
        // Arrange
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(user);

        // Act + Assert
        assertThatThrownBy(() ->
                userService.addConnection("saber@mail.com", "saber@mail.com"))
                .isInstanceOf(ConnectionAlreadyExistsException.class);
    }

    /**
     * Vérifie qu'une ConnectionAlreadyExistsException est levée
     * quand la relation existe déjà entre les deux utilisateurs.
     */
    @Test
    void addConnection_devraitLancerException_quandRelationDejaExistante() {
        // Arrange
        user.getConnections().add(connection);
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(user);
        when(userRepository.findByEmail("laure@mail.com")).thenReturn(connection);

        // Act & Assert
        assertThatThrownBy(() ->
                userService.addConnection("saber@mail.com", "laure@mail.com"))
                .isInstanceOf(ConnectionAlreadyExistsException.class);
    }
}