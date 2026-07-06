package com.paymybuddy.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalance(InsufficientBalanceException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "transfer";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "add-connection";
    }

}
