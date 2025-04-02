package com.project.appchat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "roles_salon")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleSalon {
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
    @Column(name = "role", nullable = false)
    private RoleType role;

    public enum RoleType {
        ADMIN,
        MODERATEUR,
        MEMBRE
    }
}
