package com.project.appchat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sanctions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sanction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_sanction", nullable = false)
    private SanctionType typeSanction;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modere_par", nullable = false)
    private User moderePar;

    public enum SanctionType {
        BLOCAGE_TEMPORAIRE,
        BANNISSEMENT
    }
}
