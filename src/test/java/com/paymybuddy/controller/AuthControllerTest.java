package com.paymybuddy.controller;

import com.paymybuddy.service.CustomUserDetailsService;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.paymybuddy.config.SecurityConfig;
import org.springframework.context.annotation.Import;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Vérifie que la page de connexion est accessible sans authentification
     * et retourne le statut HTTP 200.
     */
    @Test
    void login_devraitRetournerPageLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    /**
     * Vérifie que la page d'inscription est accessible sans authentification
     * et retourne le statut HTTP 200.
     */
    @Test
    void registerForm_devraitRetournerPageRegister() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    /**
     * Vérifie qu'une inscription valide appelle le service
     * et redirige vers la page de connexion.
     */
    @Test
    void register_devraitRedirigerVersLogin_apresInscriptionReussie() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "Saber")
                        .param("email", "saber@mail.com")
                        .param("password", "password123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).register(anyString(), anyString(), anyString());
    }
}