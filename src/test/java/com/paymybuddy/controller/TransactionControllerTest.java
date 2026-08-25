package com.paymybuddy.controller;

import com.paymybuddy.config.SecurityConfig;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.CustomUserDetailsService;
import com.paymybuddy.service.TransactionService;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private User user;

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
     * Vérifie que la page de transfert est accessible pour un utilisateur connecté
     * et contient les attributs nécessaires.
     */
    @Test
    void transferForm_devraitRetournerPageTransfert_pourUtilisateurConnecte() throws Exception {
        // Arrange
        when(userService.findByEmail("saber@mail.com")).thenReturn(user);
        when(transactionService.getTransactionsBySender(1)).thenReturn(new ArrayList<>());

        // Act + Assert
        mockMvc.perform(get("/transfer")
                        .with(user("saber@mail.com").password("hashedPassword")))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("transactions"));
    }

    /**
     * Vérifie qu'un transfert valide redirige vers la page de transfert.
     */
    @Test
    void transfer_devraitRedirigerApresTransfertReussi() throws Exception {
        mockMvc.perform(post("/transfer")
                        .param("receiverEmail", "laure@mail.com")
                        .param("description", "Restaurant")
                        .param("amount", "10.00")
                        .with(user("saber@mail.com").password("hashedPassword"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"));
    }

    /**
     * Vérifie qu'un montant invalide affiche un message d'erreur
     * et redirige vers la page de transfert.
     */
    @Test
    void transfer_devraitRedirigerAvecErreur_quandMontantInvalide() throws Exception {
        mockMvc.perform(post("/transfer")
                        .param("receiverEmail", "laure@mail.com")
                        .param("description", "Restaurant")
                        .param("amount", "")
                        .with(user("saber@mail.com").password("hashedPassword"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(flash().attribute("error", "Veuillez saisir un montant valide"));
    }

    /**
     * Vérifie qu'un utilisateur non connecté est redirigé vers la page de login.
     */
    @Test
    void transfer_devraitRedirigerVersLogin_pourUtilisateurNonConnecte() throws Exception {
        mockMvc.perform(get("/transfer"))
                .andExpect(status().is3xxRedirection());
    }
}