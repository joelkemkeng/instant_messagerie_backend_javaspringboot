package com.project.appchat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "salons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Salon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "nom", nullable = false, unique = true)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    private User createur;
}
