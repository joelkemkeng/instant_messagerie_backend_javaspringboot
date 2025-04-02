-- Table Utilisateurs
CREATE TABLE users (
    id UUID PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    nom_utilisateur VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    statut VARCHAR(20) DEFAULT 'HORS_LIGNE',
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(20) DEFAULT 'USER'
);

-- Table Salons
CREATE TABLE salons (
    id UUID PRIMARY KEY,
    nom VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createur_id UUID NOT NULL,
    FOREIGN KEY (createur_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table Messages
CREATE TABLE messages (
    id UUID PRIMARY KEY,
    contenu TEXT NOT NULL,
    date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expediteur_id UUID NOT NULL,
    salon_id UUID,
    destinataire_id UUID,
    FOREIGN KEY (expediteur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (salon_id) REFERENCES salons(id) ON DELETE CASCADE,
    FOREIGN KEY (destinataire_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table Rôles dans les Salons
CREATE TABLE roles_salon (
    id UUID PRIMARY KEY,
    utilisateur_id UUID NOT NULL,
    salon_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (salon_id) REFERENCES salons(id) ON DELETE CASCADE
);

-- Table Blocages et Bannissements
CREATE TABLE sanctions (
    id UUID PRIMARY KEY,
    utilisateur_id UUID NOT NULL,
    salon_id UUID NOT NULL,
    type_sanction VARCHAR(20) NOT NULL,
    date_debut TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_fin TIMESTAMP,
    modere_par UUID NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (salon_id) REFERENCES salons(id) ON DELETE CASCADE,
    FOREIGN KEY (modere_par) REFERENCES users(id) ON DELETE CASCADE
);

-- Table Notifications
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    utilisateur_id UUID NOT NULL,
    message_id UUID,
    type VARCHAR(20) NOT NULL,
    date_notification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lu BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (utilisateur_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
);
