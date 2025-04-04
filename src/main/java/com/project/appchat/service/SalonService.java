package com.project.appchat.service;

import com.project.appchat.dto.SalonDto;
import com.project.appchat.exception.ResourceNotFoundException;
import com.project.appchat.exception.UnauthorizedException;
import com.project.appchat.model.Salon;
import com.project.appchat.model.SalonRole;
import com.project.appchat.model.User;
import com.project.appchat.repository.SalonRepository;
import com.project.appchat.repository.RoleSalonRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalonService {

    private final SalonRepository salonRepository;
    private final UserRepository userRepository;
    private final RoleSalonRepository roleSalonRepository;

    @Transactional
    public SalonDto createSalon(SalonDto salonDto, String creatorEmail) {
        // Récupérer l'utilisateur créateur
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Créer un nouveau salon
        Salon salon = new Salon();
        salon.setNom(salonDto.getNom());
        salon.setDescription(salonDto.getDescription());
        salon.setDateCreation(LocalDateTime.now());
        salon.setCreateur(creator);

        // Sauvegarder le salon
        Salon savedSalon = salonRepository.save(salon);

        // Attribuer le rôle ADMIN au créateur
        com.project.appchat.model.RoleSalon roleSalon = new com.project.appchat.model.RoleSalon();
        roleSalon.setUtilisateur(creator);
        roleSalon.setSalon(savedSalon);
        roleSalon.setRole(SalonRole.ADMIN);
        roleSalonRepository.save(roleSalon);

        return mapToDto(savedSalon);
    }

    public List<SalonDto> getAllSalons() {
        return salonRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public SalonDto getSalonById(UUID id) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salon non trouvé"));
        return mapToDto(salon);
    }

    @Transactional
    public void deleteSalon(UUID id, String email) {
        Salon salon = salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salon non trouvé"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur est le créateur du salon ou un administrateur global
        boolean isCreator = salon.getCreateur().getId().equals(user.getId());
        boolean isGlobalAdmin = user.getRole() == User.UserRole.ADMIN;
        
        if (!isCreator && !isGlobalAdmin) {
            throw new UnauthorizedException("Vous n'avez pas l'autorisation de supprimer ce salon");
        }

        // Supprimer le salon
        salonRepository.delete(salon);
    }

    private SalonDto mapToDto(Salon salon) {
        SalonDto dto = new SalonDto();
        dto.setId(salon.getId());
        dto.setNom(salon.getNom());
        dto.setDescription(salon.getDescription());
        dto.setDateCreation(salon.getDateCreation());
        dto.setCreateurId(salon.getCreateur().getId());
        dto.setCreateurNom(salon.getCreateur().getNom() + " " + salon.getCreateur().getPrenom());
        return dto;
    }
}