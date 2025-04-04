package com.project.appchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    
    private UUID id;
    
    @NotBlank(message = "Le contenu du message est obligatoire")
    private String contenu;
    
    private LocalDateTime dateEnvoi;
    
    private UUID expediteurId;
    
    private String expediteurNom;
    
    private UUID salonId;
    
    private UUID destinataireId;
    
    private String destinataireNom;
    
    private boolean estPrive;
}