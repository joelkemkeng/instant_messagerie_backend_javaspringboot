package com.project.appchat.service;

import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import com.project.appchat.repository.SalonRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalonService {
    
    private final SalonRepository salonRepository;
    private final UserRepository userRepository; // Ajout du UserRepository
    private final MessageService messageService;

    public List<Salon> findAll() {
        return salonRepository.findAll();
    }

    public Salon save(Salon salon) {
        return salonRepository.save(salon);
    }

    public Salon findById(UUID id) {
        return salonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));
    }

    public void joinSalon(UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        Salon salon = findById(id);
        if (!salon.getUsers().contains(user)) {
            salon.getUsers().add(user);
            salonRepository.save(salon);
        }
    }

    public void leaveSalon(UUID id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        Salon salon = findById(id);
        if (salon.getUsers().contains(user)) {
            salon.getUsers().remove(user);
            salonRepository.save(salon);
        }
    }
    
    public List<Salon> searchSalons(String query) {
        return salonRepository.findByNomContainingIgnoreCase(query)
                .orElseGet(List::of);
    }
}