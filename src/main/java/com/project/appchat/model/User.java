package com.project.appchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "createdSalons", "joinedSalons", "messages"})
public class User {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private UserStatus statut = UserStatus.HORS_LIGNE;
    
    @Column(name = "date_inscription")
    private LocalDateTime dateInscription = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.USER;

    // Relations avec les salons
    @OneToMany(mappedBy = "createur", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"createur", "users", "messages"})
    private Set<Salon> createdSalons = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_salons",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "salon_id")
    )
    @JsonIgnoreProperties({"createur", "users", "messages"})
    private Set<Salon> joinedSalons = new HashSet<>();

    // Relations avec les messages
    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"expediteur", "destinataire", "salon"})
    private Set<Message> messages = new HashSet<>();

    public enum UserStatus {
        EN_LIGNE, HORS_LIGNE
    }
    
    public enum UserRole {
        USER, ADMIN
    }
}