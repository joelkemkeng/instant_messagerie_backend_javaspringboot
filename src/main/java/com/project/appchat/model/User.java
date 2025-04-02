package com.project.appchat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "nom_utilisateur", nullable = false)
    private String nomUtilisateur;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private UserStatus statut;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    public enum UserStatus {
        EN_LIGNE,
        HORS_LIGNE
    }

    public enum UserRole {
        USER,
        ADMIN
    }
}
