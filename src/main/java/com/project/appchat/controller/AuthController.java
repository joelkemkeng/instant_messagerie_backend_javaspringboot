package com.project.appchat.controller;

import com.project.appchat.model.User;
import com.project.appchat.service.UserService;
import com.project.appchat.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.register(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            
            User fullUser = userService.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            String token = jwtTokenProvider.generateToken(authentication);
            
            return ResponseEntity.ok(new AuthResponse(
                token,
                fullUser.getId(),
                fullUser.getEmail(),
                fullUser.getNom(),
                fullUser.getPrenom()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Identifiants invalides");
        }
    }

    private static class AuthResponse {
        private final String token;
        private final UUID userId;
        private final String email;
        private final String nom;
        private final String prenom;

        public AuthResponse(String token, UUID userId, String email, String nom, String prenom) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.nom = nom;
            this.prenom = prenom;
        }

        // Getters nécessaires pour la sérialisation JSON
        public String getToken() { return token; }
        public UUID getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
    }
}