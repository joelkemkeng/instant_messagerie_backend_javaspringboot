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
@Table(name = "salons")
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
    
    @ManyToOne
    @JoinColumn(name = "createur_id", nullable = false)
    private User createur;
}
