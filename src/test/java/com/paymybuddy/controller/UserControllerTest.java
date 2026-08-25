package com.paymybuddy.controller;

import com.paymybuddy.config.SecurityConfig;
import com.paymybuddy.model.User;
import com.paymybuddy.service.CustomUserDetailsService;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    /**
     * Initialise les données de test réutilisées dans tous les tests.
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
     * Vérifie que la page de profil est accessible pour un utilisateur connecté
     * et affiche les informations de l'utilisateur.
     */
    @Test
    void profile_devraitRetournerPageProfil_pourUtilisateurConnecte() throws Exception {
        // Arrange
        when(userService.findByEmail("saber@mail.com")).thenReturn(user);

        // Act + Assert
        mockMvc.perform(get("/profile")
                        .with(user("saber@mail.com").password("hashedPassword")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }

    /**
     * Vérifie que la page d'ajout de relation est accessible
     * pour un utilisateur connecté.
     */
    @Test
    void addConnectionForm_devraitRetournerPageAjoutRelation() throws Exception {
        mockMvc.perform(get("/add-connection")
                        .with(user("saber@mail.com").password("hashedPassword")))
                .andExpect(status().isOk())
                .andExpect(view().name("add-connection"));
    }

    /**
     * Vérifie qu'une relation valide est ajoutée et redirige
     * vers la page d'ajout de relation.
     */
    @Test
    void addConnection_devraitRedirigerApresAjout() throws Exception {
        mockMvc.perform(post("/add-connection")
                        .param("connectionEmail", "laure@mail.com")
                        .with(user("saber@mail.com").password("hashedPassword"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add-connection"));

        verify(userService, times(1)).addConnection(anyString(), anyString());
    }

    /**
     * Vérifie qu'un utilisateur non connecté est redirigé vers la page de login.
     */
    @Test
    void profile_devraitRedirigerVersLogin_pourUtilisateurNonConnecte() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection());
    }
}