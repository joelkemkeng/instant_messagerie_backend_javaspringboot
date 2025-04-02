package com.project.appchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles_salon")
public class RoleSalon {
    
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
    @Column(name = "role", nullable = false)
    private SalonRole role;
}
