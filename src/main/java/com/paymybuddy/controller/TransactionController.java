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

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping("/transfer")
    public String transferForm(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName());
        List<Transaction> transactions = transactionService.getTransactionsBySender(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(Authentication authentication,
                           @RequestParam String receiverEmail,
                           @RequestParam String description,
                           @RequestParam BigDecimal amount) {
        transactionService.transfert(authentication.getName(), receiverEmail, description, amount);
        return "redirect:/transfer";
    }
}
