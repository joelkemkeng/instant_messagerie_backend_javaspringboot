package com.project.appchat.dto;

import com.project.appchat.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UUID userId;
    private String email;
    private String nom;
    private String prenom;
    private User.UserRole role;
    private String token;
}