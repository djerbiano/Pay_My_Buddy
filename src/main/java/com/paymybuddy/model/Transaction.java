package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Expéditeur du transfert — relation ManyToOne :
     * un User peut être sender de plusieurs transactions,
     * mais une transaction n'a qu'un seul sender.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Destinataire du transfert — relation ManyToOne :
     * un User peut être receiver de plusieurs transactions,
     * mais une transaction n'a qu'un seul receiver.
     */
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /**
     * Description optionnelle du transfert
     */
    @Column(length = 255)
    private String description;

    /**
     * Montant en DECIMAL(10,2) — choix délibéré vs double
     * pour éviter les erreurs d'arrondi sur les montants monétaires.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}
