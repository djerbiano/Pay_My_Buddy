-- SAUVEGARDE : mysqldump -u root -p paymybuddy > backup.sql
-- RESTAURATION : mysql -u root -p paymybuddy < backup.sql

CREATE DATABASE IF NOT EXISTS paymybuddy;
USE paymybuddy;

CREATE TABLE user (
    id INT AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id)
);

CREATE TABLE connection (
    user_id INT NOT NULL,
    connection_id INT NOT NULL,
    PRIMARY KEY (user_id, connection_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (connection_id) REFERENCES user(id)
);

CREATE TABLE transaction (
    id INT AUTO_INCREMENT,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (sender_id) REFERENCES user(id),
    FOREIGN KEY (receiver_id) REFERENCES user(id)
);

-- Données de test : utilisateurs
INSERT INTO user (username, email, password, balance) VALUES
('Saber', 'saber@mail.com', 'password123', 100.00),
('Laure-Anne', 'laure@mail.com', 'password123', 50.00),
('Clara', 'clara@mail.com', 'password123', 75.00),
('Luc', 'luc@mail.com', 'password123', 30.00);

-- Données de test : relations
INSERT INTO connection (user_id, connection_id) VALUES
(1, 2),
(1, 3),
(1, 4);

-- Données de test : transactions
INSERT INTO transaction (sender_id, receiver_id, description, amount) VALUES
(1, 2, 'Restaurant', 10.00),
(1, 3, 'Voyage', 25.00),
(1, 4, 'Billets de cinéma', 8.00);

-- Table invoice (non implémentée dans ce prototype)
-- Prévue pour la future fonctionnalité de facturation (V1)
-- CREATE TABLE invoice (
--     id INT AUTO_INCREMENT,
--     transaction_id INT NOT NULL,
--     amount DECIMAL(10,2) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     PRIMARY KEY (id),
--     FOREIGN KEY (transaction_id) REFERENCES transaction(id)
-- );