package com.project.appchat.service;

import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import com.project.appchat.repository.SalonRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalonService {
    
    private final SalonRepository salonRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Salon createSalon(String nom, String description, String creatorUsername) {
        User creator = userRepository.findByNomUtilisateur(creatorUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Salon salon = Salon.builder()
                .nom(nom)
                .description(description)
                .dateCreation(LocalDateTime.now())
                .createur(creator)
                .build();
        
        return salonRepository.save(salon);
    }
    
    @Transactional(readOnly = true)
    public List<Salon> getAllSalons() {
        return salonRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Salon> getSalonById(UUID id) {
        return salonRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Salon> getSalonByNom(String nom) {
        return salonRepository.findByNom(nom);
    }
    
    @Transactional
    public void deleteSalon(UUID id, String username) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));
        
        User user = userRepository.findByNomUtilisateur(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        if (!salon.getCreateur().getId().equals(user.getId())) {
            throw new RuntimeException("Seul le créateur peut supprimer le salon");
        }
        
        salonRepository.delete(salon);
    }
}
