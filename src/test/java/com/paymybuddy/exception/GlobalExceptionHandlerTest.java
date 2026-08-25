package com.paymybuddy.exception;

import com.paymybuddy.config.SecurityConfig;
import com.paymybuddy.controller.UserController;
import com.paymybuddy.service.CustomUserDetailsService;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Vérifie que UserNotFoundException est gérée par le GlobalExceptionHandler
     * et retourne la vue add-connection avec un message d'erreur.
     */
    @Test
    void addConnection_devraitAfficherErreur_quandUserNonTrouve() throws Exception {
        // Arrange
        doThrow(new UserNotFoundException("Aucun utilisateur trouvé"))
                .when(userService).addConnection(anyString(), anyString());

        // Act + Assert
        mockMvc.perform(post("/add-connection")
                        .param("connectionEmail", "inconnu@mail.com")
                        .with(user("saber@mail.com").password("hashedPassword"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-connection"))
                .andExpect(model().attribute("error", "Aucun utilisateur trouvé"));
    }

    /**
     * Vérifie que ConnectionAlreadyExistsException est gérée par le GlobalExceptionHandler
     * et retourne la vue add-connection avec un message d'erreur.
     */
    @Test
    void addConnection_devraitAfficherErreur_quandRelationDejaExistante() throws Exception {
        // Arrange
        doThrow(new ConnectionAlreadyExistsException("Cette relation existe déjà"))
                .when(userService).addConnection(anyString(), anyString());

        // Act + Assert
        mockMvc.perform(post("/add-connection")
                        .param("connectionEmail", "laure@mail.com")
                        .with(user("saber@mail.com").password("hashedPassword"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-connection"))
                .andExpect(model().attribute("error", "Cette relation existe déjà"));
    }


}