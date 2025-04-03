package com.project.appchat.service;

import com.project.appchat.dto.AuthRequest;
import com.project.appchat.dto.AuthResponse;
import com.project.appchat.dto.RegisterRequest;
import com.project.appchat.exception.ResourceAlreadyExistsException;
import com.project.appchat.model.User;
import com.project.appchat.repository.UserRepository;
import com.project.appchat.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Un compte avec cet email existe déjà");
        }

        logger.info("Création d'un nouvel utilisateur avec email: {}", registerRequest.getEmail());
        
        // Créer un nouvel utilisateur
        User user = new User();
        user.setNom(registerRequest.getNom());
        user.setPrenom(registerRequest.getPrenom());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        // Définir le rôle (USER par défaut si non spécifié)
        if (registerRequest.getRole() != null) {
            user.setRole(registerRequest.getRole());
        }
        
        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès: {}", savedUser.getId());

        // Générer le token JWT
        String token = tokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());

        // Créer et retourner la réponse
        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNom(),
                savedUser.getPrenom(),
                savedUser.getRole(),
                token
        );
    }

    public AuthResponse login(AuthRequest loginRequest) {
        logger.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            // Mettre à jour le statut de l'utilisateur
            user.setStatut(User.UserStatus.EN_LIGNE);
            userRepository.save(user);

            // Générer le token JWT
            String token = tokenProvider.generateToken(authentication);
            
            logger.info("Connexion réussie pour l'utilisateur: {}", user.getId());

            // Créer et retourner la réponse
            return new AuthResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getRole(),
                    token
            );
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification: {}", e.getMessage());
            throw e;
        }
    }
    
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Mettre à jour le statut de l'utilisateur
        user.setStatut(User.UserStatus.HORS_LIGNE);
        userRepository.save(user);
    }
}