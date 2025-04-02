package com.project.appchat.config;

import com.project.appchat.model.User;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Vérifier si des utilisateurs existent déjà
            if (userRepository.count() == 0) {
                System.out.println("Initialisation des données de test...");
                
                // Créer des utilisateurs de test
                User user1 = new User();
                user1.setId(UUID.randomUUID());
                user1.setNom("Doe");
                user1.setPrenom("John");
                user1.setNomUtilisateur("john");
                user1.setEmail("john@example.com");
                user1.setMotDePasse("password");
                user1.setStatut(User.UserStatus.EN_LIGNE);
                user1.setDateInscription(LocalDateTime.now());
                user1.setRole(User.UserRole.USER);
                
                User user2 = new User();
                user2.setId(UUID.randomUUID());
                user2.setNom("Smith");
                user2.setPrenom("Tony");
                user2.setNomUtilisateur("tony");
                user2.setEmail("tony@example.com");
                user2.setMotDePasse("password");
                user2.setStatut(User.UserStatus.EN_LIGNE);
                user2.setDateInscription(LocalDateTime.now());
                user2.setRole(User.UserRole.USER);
                
                // Sauvegarder les utilisateurs
                userRepository.save(user1);
                userRepository.save(user2);
                
                System.out.println("Données de test initialisées avec succès !");
            }
        };
    }
}
