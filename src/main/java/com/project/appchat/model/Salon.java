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
@Table(name = "salons")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "messages", "joinedUsers"})
public class Salon {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(name = "nom", nullable = false, unique = true, length = 100)
    private String nom;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    @JsonIgnoreProperties({"createdSalons", "joinedSalons", "messages"})
    private User createur;

    @ManyToMany(mappedBy = "joinedSalons")
    @JsonIgnoreProperties({"createdSalons", "joinedSalons", "messages"})
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"salon", "expediteur", "destinataire"})
    private Set<Message> messages = new HashSet<>();
}