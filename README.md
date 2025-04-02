# Application de Chat en Temps Réel - Backend

Une application de messagerie instantanée construite avec Spring Boot, WebSocket et PostgreSQL.

## Prérequis

- Java 21
- Maven
- PostgreSQL 16
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## Configuration de la Base de Données

1. Installer PostgreSQL si ce n'est pas déjà fait
2. Créer une base de données :
   ```sql
   CREATE DATABASE appchat;
   ```
3. Configurer les accès dans `src/main/resources/application.properties` :
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/appchat
   spring.datasource.username=postgres
   spring.datasource.password=admin
   ```

## Installation et Démarrage

1. Cloner le projet :
   ```bash
   git clone https://github.com/joelkemkeng/instant_messagerie_backend_javaspringboot.git
   cd instant_messagerie_backend_javaspringboot
   ```

2. Installer les dépendances :
   ```bash
   mvn clean install
   ```

3. Démarrer l'application :
   ```bash
   mvn spring-boot:run
   ```

4. L'application sera accessible à : http://localhost:8080

## Fonctionnalités Implémentées

- ✅ Configuration de la base de données PostgreSQL
- ✅ Modèles de données (User, Salon, Message, etc.)
- ✅ Communication en temps réel via WebSocket
- ✅ Persistance des messages
- ✅ Messages privés
- ✅ Gestion des salons de discussion
- 🔄 Authentification (en cours par un autre membre de l'équipe)

## Structure du Projet

```
src/main/java/com/project/appchat/
├── model/
│   ├── User.java         (Modèle utilisateur)
│   ├── Salon.java       (Modèle salon de discussion)
│   ├── Message.java     (Modèle message persistant)
│   └── ChatMessage.java (Modèle message WebSocket)
├── repository/
│   ├── MessageRepository.java
│   └── SalonRepository.java
├── service/
│   ├── ChatService.java    (Gestion WebSocket)
│   └── MessageService.java (Gestion persistence)
└── config/
    └── WebSocketConfig.java
```

## Comment Tester

1. **Créer un Utilisateur** :
   - Via pgAdmin 4, exécuter :
   ```sql
   INSERT INTO users (id, nom, prenom, nom_utilisateur, email, mot_de_passe, statut, date_inscription, role)
   VALUES (gen_random_uuid(), 'Yonke', 'Tony', 'tonnyk', 'tonnyk@mail.com', 'password', 'EN_LIGNE', NOW(), 'USER');
   ```

2. **Se Connecter** :
   - Ouvrir http://localhost:8080
   - Utiliser les identifiants créés

3. **Tester les Messages en Temps Réel** :
   - Ouvrir deux navigateurs différents
   - Se connecter avec deux comptes différents
   - Envoyer des messages dans un salon
   - Vérifier la réception en temps réel

4. **Vérifier la Persistance** :
   - Envoyer des messages
   - Rafraîchir la page
   - Les messages doivent toujours être visibles
   - Vérifier dans la base de données :
   ```sql
   SELECT * FROM messages ORDER BY date_envoi DESC;
   ```

## État d'Avancement

- Backend : ~35% complété
- Prochaines étapes :
  - Finalisation de l'authentification
  - Gestion des utilisateurs
  - Modération des salons
  - API REST complète

## Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push sur la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request
