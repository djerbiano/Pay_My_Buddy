package com.paymybuddy.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "add-connection";
    }

    @ExceptionHandler(ConnectionAlreadyExistsException.class)
    public String handleConnectionAlreadyExists(ConnectionAlreadyExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "add-connection";
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExists(EmailAlreadyExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "register";
    }
}
