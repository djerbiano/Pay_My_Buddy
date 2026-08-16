package com.paymybuddy.service;

import com.paymybuddy.exception.ConnectionAlreadyExistsException;
import com.paymybuddy.exception.EmailAlreadyExistsException;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Inscrit un nouvel utilisateur après vérification que l'email n'est pas déjà utilisé.
     * Le mot de passe est hashé avec BCrypt avant la sauvegarde.
     */
    @Transactional
    public User register(String username, String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyExistsException("Veuillez utiliser une autre adresse email");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    /**
     * Ajoute une relation entre deux utilisateurs.
     * Vérifie que la relation n'existe pas déjà et que l'utilisateur
     * ne tente pas de s'ajouter lui-même.
     */
    @Transactional
    public void addConnection(String userEmail, String connectionEmail) {
        User user = userRepository.findByEmail(userEmail);
        User connection = userRepository.findByEmail(connectionEmail);
        if (connection == null) {
            throw new UserNotFoundException("Aucun utilisateur trouvé avec l'email : " + connectionEmail);
        }
        if (userEmail.equals(connectionEmail)) {
            throw new ConnectionAlreadyExistsException("Vous ne pouvez pas vous ajouter vous-même");
        }
        if (user.getConnections().contains(connection)) {
            throw new ConnectionAlreadyExistsException("Cette relation existe déjà");
        }
        user.getConnections().add(connection);
        userRepository.save(user);
    }

    /**
     * Recherche un utilisateur par son email.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
