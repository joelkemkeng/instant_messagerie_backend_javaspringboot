package com.project.appchat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(name = "contenu", nullable = false)
    private String contenu;
    
    @Column(name = "date_envoi")
    @Builder.Default
    private LocalDateTime dateEnvoi = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    @JsonIgnoreProperties({"createdSalons", "joinedSalons", "messages"})
    private User expediteur;
    
    @ManyToOne
    @JoinColumn(name = "salon_id")
    @JsonIgnoreProperties({"createur", "users", "messages"})
    private Salon salon;
    
    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    @JsonIgnoreProperties({"createdSalons", "joinedSalons", "messages"})
    private User destinataire;
}