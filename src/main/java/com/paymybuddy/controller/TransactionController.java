package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    /**
     * Affiche la page de transfert avec la liste des relations
     * et l'historique des transactions de l'utilisateur connecté.
     */
    @GetMapping("/transfer")
    public String transferForm(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName());
        List<Transaction> transactions = transactionService.getTransactionsBySender(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        return "transfer";
    }

    /**
     * Traite le formulaire de transfert.
     * Valide la relation et le montant avant de déléguer au service.
     * Utilise RedirectAttributes pour conserver le message d'erreur
     * après le redirect vers GET /transfer.
     */
    @PostMapping("/transfer")
    public String transfer(Authentication authentication,
                           @RequestParam String receiverEmail,
                           @RequestParam String description,
                           @RequestParam(required = false) BigDecimal amount,
                           RedirectAttributes redirectAttributes) {
        try {
            if (receiverEmail == null || receiverEmail.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner une relation");
                return "redirect:/transfer";
            }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Veuillez saisir un montant valide");
                return "redirect:/transfer";
            }
            transactionService.transfert(authentication.getName(), receiverEmail, description, amount);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/transfer";
    }
}
