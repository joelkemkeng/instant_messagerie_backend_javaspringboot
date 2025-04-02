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
   INSERT INTO users (id, nom, prenom, nom_utilisateur, email, password, statut, date_inscription, role)
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

## Tester les Endpoints avec Postman

Voici comment tester les différentes fonctionnalités de l'API avec Postman :

### 1. Authentification

#### Inscription (POST /api/auth/register)
- Méthode : POST
- URL : http://localhost:8080/api/auth/register
- Headers : 
  - Content-Type : application/json
- Body (raw, JSON) :
```json
{
    "nom": "Dupont",
    "prenom": "Jean",
    "email": "jean.dupont@example.com",
    "password": "motdepasse123"
}
```

#### Connexion (POST /api/auth/login)
- Méthode : POST
- URL : http://localhost:8080/api/auth/login
- Headers : 
  - Content-Type : application/json
- Body (raw, JSON) :
```json
{
    "email": "jean.dupont@example.com",
    "password": "motdepasse123"
}
```
- Réponse : Token JWT à utiliser pour les requêtes suivantes

### 2. Gestion des Salons

#### Créer un Salon (POST /api/salons)
- Méthode : POST
- URL : http://localhost:8080/api/salons
- Headers : 
  - Content-Type : application/json
  - Authorization : Bearer {votre_token_jwt}
- Body (raw, JSON) :
```json
{
    "nom": "Salon Général",
    "description": "Discussion générale"
}
```

#### Lister les Salons (GET /api/salons)
- Méthode : GET
- URL : http://localhost:8080/api/salons
- Headers : 
  - Authorization : Bearer {votre_token_jwt}

### 3. Messages

#### Envoyer un Message dans un Salon (POST /api/messages)
- Méthode : POST
- URL : http://localhost:8080/api/messages
- Headers : 
  - Content-Type : application/json
  - Authorization : Bearer {votre_token_jwt}
- Body (raw, JSON) :
```json
{
    "contenu": "Bonjour tout le monde !",
    "salonId": "uuid_du_salon"
}
```

#### Envoyer un Message Privé (POST /api/messages/private)
- Méthode : POST
- URL : http://localhost:8080/api/messages/private
- Headers : 
  - Content-Type : application/json
  - Authorization : Bearer {votre_token_jwt}
- Body (raw, JSON) :
```json
{
    "contenu": "Message privé",
    "destinataireId": "uuid_du_destinataire"
}
```

### 4. Gestion des Utilisateurs

#### Lister les Utilisateurs (GET /api/users)
- Méthode : GET
- URL : http://localhost:8080/api/users
- Headers : 
  - Authorization : Bearer {votre_token_jwt}

#### Récupérer un Utilisateur (GET /api/users/{id})
- Méthode : GET
- URL : http://localhost:8080/api/users/{id}
- Headers : 
  - Authorization : Bearer {votre_token_jwt}

### Conseils d'Utilisation

1. Sauvegarder les requêtes dans une Collection Postman
2. Utiliser l'environnement Postman pour stocker le token JWT
3. Vérifier les réponses JSON pour le débogage
4. Utiliser les variables d'environnement pour les URLs de base

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
