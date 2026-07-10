## Modèle Physique de Données (MPD)

Le MPD ci-dessous est conçu à partir du diagramme UML fourni. Il définit les tables,
colonnes, types et contraintes nécessaires à l'implémentation de la base de données
relationnelle (MySQL).

### Table `user`

| Colonne  | Type          | Contraintes                  |
|----------|---------------|-------------------------------|
| id       | INT           | PK, AUTO_INCREMENT           |
| username | VARCHAR(50)   | NOT NULL                     |
| email    | VARCHAR(100)  | NOT NULL, UNIQUE             |
| password | VARCHAR(255)  | NOT NULL                     |
| balance  | DECIMAL(10,2) | NOT NULL, DEFAULT 0.00       |

> Note : le champ `balance` n'apparaît pas dans le diagramme UML fourni, mais a été
> ajouté car indispensable à la gestion de l'argent (paiements basés sur le solde
> disponible).

### Table `connection`

Table de jointure matérialisant la relation many-to-many réflexive `connections`
du diagramme UML (association unidirectionnelle : un utilisateur ajoute une relation
sans réciprocité automatique).

| Colonne        | Type | Contraintes              |
|----------------|------|---------------------------|
| user_id        | INT  | FK → user.id              |
| connection_id  | INT  | FK → user.id               |

Clé primaire composite : `(user_id, connection_id)`

### Table `transaction`

| Colonne     | Type          | Contraintes                |
|-------------|---------------|------------------------------|
| id          | INT           | PK, AUTO_INCREMENT          |
| sender_id   | INT           | FK → user.id, NOT NULL      |
| receiver_id | INT           | FK → user.id, NOT NULL      |
| description | VARCHAR(255)  |                              |
| amount      | DECIMAL(10,2) | NOT NULL                    |

> Note : le type `DECIMAL(10,2)` est utilisé à la place du `double` du diagramme UML
> pour éviter les erreurs d'arrondi propres aux flottants binaires, ce qui est une
> bonne pratique pour manipuler des montants monétaires.
