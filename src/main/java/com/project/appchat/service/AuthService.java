package com.project.appchat.service;

import com.project.appchat.dto.AuthRequest;
import com.project.appchat.dto.AuthResponse;
import com.project.appchat.dto.RegisterRequest;
import com.project.appchat.exception.ResourceAlreadyExistsException;
import com.project.appchat.model.User;
import com.project.appchat.repository.UserRepository;
import com.project.appchat.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Un compte avec cet email existe déjà");
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setNom(registerRequest.getNom());
        user.setPrenom(registerRequest.getPrenom());
        user.setEmail(registerRequest.getEmail());
        user.setMotDePasse(passwordEncoder.encode(registerRequest.getPassword()));
        
        // Définir le rôle (USER par défaut si non spécifié)
        if (registerRequest.getRole() != null) {
            user.setRole(registerRequest.getRole());
        }
        
        User savedUser = userRepository.save(user);

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

        // Créer et retourner la réponse
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getRole(),
                token
        );
    }
    
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Mettre à jour le statut de l'utilisateur
        user.setStatut(User.UserStatus.HORS_LIGNE);
        userRepository.save(user);
    }
}