package com.paymybuddy.service;

import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public void addConnection(String userEmail, String connectionEmail) {
        User user = userRepository.findByEmail(userEmail);
        User connection = userRepository.findByEmail(connectionEmail);
        if (connection == null) {
            throw new UserNotFoundException("Aucun utilisateur trouvé avec l'email : " + connectionEmail);
        }
        user.getConnections().add(connection);
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
