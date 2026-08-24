package com.paymybuddy.service;

import com.paymybuddy.exception.InsufficientBalanceException;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;

    /**
     * Initialise les données de test réutilisées dans tous les tests.
     * Appelé automatiquement avant chaque méthode de test.
     */
    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1);
        sender.setUsername("Saber");
        sender.setEmail("saber@mail.com");
        sender.setBalance(BigDecimal.valueOf(100));
        sender.setConnections(new ArrayList<>());

        receiver = new User();
        receiver.setId(2);
        receiver.setUsername("Laure-Anne");
        receiver.setEmail("laure@mail.com");
        receiver.setBalance(BigDecimal.valueOf(50));
        receiver.setConnections(new ArrayList<>());
    }

    /**
     * Vérifie qu'un transfert valide débite le sender,
     * crédite le receiver et sauvegarde la transaction.
     */
    @Test
    void transfert_devraitEffectuerLeTransfert_quandSoldeEstSuffisant() {
        // Arrange
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(sender);
        when(userRepository.findByEmail("laure@mail.com")).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        transactionService.transfert("saber@mail.com", "laure@mail.com", "Restaurant", BigDecimal.valueOf(30));

        // Assert
        assertThat(sender.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(70));
        assertThat(receiver.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(80));
        verify(userRepository, times(2)).save(any(User.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    /**
     * Vérifie qu'une InsufficientBalanceException est levée
     * quand le solde du sender est insuffisant.
     * Vérifie aussi qu'aucune sauvegarde n'est effectuée.
     */
    @Test
    void transfert_devraitLancerException_quandSoldeInsuffisant() {
        // Arrange
        when(userRepository.findByEmail("saber@mail.com")).thenReturn(sender);
        when(userRepository.findByEmail("laure@mail.com")).thenReturn(receiver);

        // Act + Assert
        assertThatThrownBy(() ->
                transactionService.transfert("saber@mail.com", "laure@mail.com", "Restaurant", BigDecimal.valueOf(200)))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    /**
     * Vérifie que getTransactionsBySender retourne bien
     * la liste des transactions d'un utilisateur.
     */
    @Test
    void getTransactionsBySender_devraitRetournerLesTransactions() {
        // Arrange
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        transactions.add(new Transaction());
        when(transactionRepository.findBySenderId(1)).thenReturn(transactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsBySender(1);

        // Assert
        assertThat(result).hasSize(2);
        verify(transactionRepository, times(1)).findBySenderId(1);
    }
}