package com.project.appchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    
    private UUID id;
    
    private UUID userId;
    
    private String nom;
    
    private String prenom;
    
    private String email;
    
    private String role;
    
    private String statut;
    
    private Boolean isCreator;
}