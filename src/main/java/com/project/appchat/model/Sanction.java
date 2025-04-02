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
@Table(name = "sanctions")
public class Sanction {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;
    
    @ManyToOne
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_sanction", nullable = false)
    private TypeSanction typeSanction;
    
    @Column(name = "date_debut")
    private LocalDateTime dateDebut = LocalDateTime.now();
    
    @Column(name = "date_fin")
    private LocalDateTime dateFin;
    
    @ManyToOne
    @JoinColumn(name = "modere_par", nullable = false)
    private User moderePar;
    
    public enum TypeSanction {
        BLOCAGE_TEMPORAIRE, BANNISSEMENT
    }
}