package com.paymybuddy.service;

import com.paymybuddy.exception.InsufficientBalanceException;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Effectue un transfert d'argent entre deux utilisateurs.
     * Vérifie que le solde de l'expéditeur est suffisant avant de débiter.
     * L'opération est atomique grâce à @Transactional :
     * si une erreur survient, tout est annulé (rollback automatique).
     */
    @Transactional
    public void transfert(String senderEmail, String receiverEmail, String description, BigDecimal amount) {
        User sender = userRepository.findByEmail(senderEmail);
        User receiver = userRepository.findByEmail(receiverEmail);

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Solde insuffisant");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription(description);
        transaction.setAmount(amount);

        userRepository.save(sender);
        userRepository.save(receiver);
        transactionRepository.save(transaction);
    }

    /**
     * Récupère toutes les transactions effectuées par un utilisateur.
     */
    public List<Transaction> getTransactionsBySender(Integer senderId) {
        return transactionRepository.findBySenderId(senderId);
    }

}
