package com.project.appchat.service;

import com.project.appchat.model.Salon;
import com.project.appchat.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalonService {
    
    private final SalonRepository salonRepository;
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
        Salon salon = findById(id);
        // Logique pour rejoindre le salon
    }

    public void leaveSalon(UUID id) {
        Salon salon = findById(id);
        // Logique pour quitter le salon
    }

    public List<Salon> searchSalons(String query) {
        return salonRepository.findByNomContainingIgnoreCase(query)
                .orElseGet(List::of);
    }
}