package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String username;
    /**
     * Email unique utilisé comme identifiant de connexion
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Solde du compte en DECIMAL(10,2) — choix délibéré vs double
     * pour éviter les erreurs d'arrondi sur les montants monétaires.
     * Initialisé à zéro à la création du compte.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Liste des relations de l'utilisateur — relation many-to-many réflexive.
     * Unidirectionnelle : A ajoute B sans réciprocité automatique.
     * Initialisée à ArrayList vide pour éviter les NullPointerException
     * sur les nouveaux utilisateurs sans connexion.
     */
    @ManyToMany
    @JoinTable(
            name = "connection",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id")
    )
    private List<User> connections = new ArrayList<>();
}
