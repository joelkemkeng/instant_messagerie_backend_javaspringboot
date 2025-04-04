package com.project.appchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalonDto {
    
    private UUID id;
    
    @NotBlank(message = "Le nom du salon est obligatoire")
    @Size(max = 100, message = "Le nom du salon ne peut pas dépasser 100 caractères")
    private String nom;
    
    private String description;
    
    private LocalDateTime dateCreation;
    
    private UUID createurId;
    
    private String createurNom;
}