package com.project.appchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private UUID id;
    
    private String nom;
    
    private String prenom;
    
    private String email;
    
    private String statut;
    
    private String role;
    
    private LocalDateTime dateInscription;
}