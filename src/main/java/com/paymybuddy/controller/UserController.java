package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Affiche la page de profil de l'utilisateur connecté.
     * Récupère le User depuis la base via son email (fourni par Spring Security).
     */
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Affiche le formulaire d'ajout de relation
     */
    @GetMapping("/add-connection")
    public String addConnectionForm() {
        return "add-connection";
    }

    /**
     * Traite le formulaire d'ajout de relation.
     * Délègue la logique métier à UserService.
     * Redirige vers /add-connection après ajout réussi.
     */
    @PostMapping("/add-connection")
    public String addConnection(Authentication authentication, @RequestParam String connectionEmail) {
        userService.addConnection(authentication.getName(), connectionEmail);
        return "redirect:/add-connection";
    }
}
