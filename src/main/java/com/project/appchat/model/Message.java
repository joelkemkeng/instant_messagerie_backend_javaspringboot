package com.project.appchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(name = "contenu", nullable = false)
    private String contenu;
    
    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private User expediteur;
    
    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;
    
    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private User destinataire;
}
