package com.project.appchat.service;

import com.project.appchat.dto.MemberDto;
import com.project.appchat.dto.UserDto;
import com.project.appchat.exception.ResourceAlreadyExistsException;
import com.project.appchat.exception.ResourceNotFoundException;
import com.project.appchat.exception.UnauthorizedException;
import com.project.appchat.model.RoleSalon;
import com.project.appchat.model.Salon;
import com.project.appchat.model.SalonRole;
import com.project.appchat.model.User;
import com.project.appchat.repository.RoleSalonRepository;
import com.project.appchat.repository.SalonRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final RoleSalonRepository roleSalonRepository;
    private final UserRepository userRepository;
    private final SalonRepository salonRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<MemberDto> getSalonMembers(UUID salonId) {
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon non trouvé"));
        
        return roleSalonRepository.findBySalon(salon).stream()
                .map(this::mapToMemberDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MemberDto addMemberToSalon(UUID salonId, UUID userId, String requestingUserEmail) {
        User requestingUser = userRepository.findByEmail(requestingUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur demandeur non trouvé"));
        
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon non trouvé"));
        
        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur à ajouter non trouvé"));
        
        // Vérifier si l'utilisateur a déjà un rôle dans ce salon
        if (roleSalonRepository.existsBySalonAndUtilisateur(salon, userToAdd)) {
            throw new ResourceAlreadyExistsException("Cet utilisateur est déjà membre du salon");
        }
        
        // Vérifier que l'utilisateur qui fait la demande est admin ou modérateur du salon
        // ou admin global (sauf si c'est un utilisateur qui veut se joindre lui-même)
        boolean isAdmin = requestingUser.getRole() == User.UserRole.ADMIN;
        boolean isSameUser = requestingUser.getId().equals(userToAdd.getId());
        
        if (!isAdmin && !isSameUser) {
            RoleSalon roleSalon = roleSalonRepository.findBySalonAndUtilisateur(salon, requestingUser)
                    .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de ce salon"));
            
            if (roleSalon.getRole() != SalonRole.ADMIN && roleSalon.getRole() != SalonRole.MODERATEUR) {
                throw new UnauthorizedException("Vous n'avez pas le droit d'ajouter des membres à ce salon");
            }
        }
        
        // Ajouter l'utilisateur comme membre du salon
        RoleSalon newRoleSalon = new RoleSalon();
        newRoleSalon.setSalon(salon);
        newRoleSalon.setUtilisateur(userToAdd);
        newRoleSalon.setRole(SalonRole.MEMBRE);
        
        RoleSalon savedRoleSalon = roleSalonRepository.save(newRoleSalon);
        
        MemberDto memberDto = mapToMemberDto(savedRoleSalon);
        
        // Notifier les membres du salon qu'un nouveau membre a rejoint
        messagingTemplate.convertAndSend("/topic/salon/" + salon.getId() + "/members", memberDto);
        
        return memberDto;
    }
    
    @Transactional
    public MemberDto updateMemberRole(UUID salonId, UUID userId, SalonRole newRole, String requestingUserEmail) {
        User requestingUser = userRepository.findByEmail(requestingUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur demandeur non trouvé"));
        
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon non trouvé"));
        
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur cible non trouvé"));
        
        // Vérifier que l'utilisateur cible est membre du salon
        RoleSalon targetRoleSalon = roleSalonRepository.findBySalonAndUtilisateur(salon, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("Cet utilisateur n'est pas membre du salon"));
        
        // Vérifier que l'utilisateur demandeur est admin du salon ou admin global
        boolean isGlobalAdmin = requestingUser.getRole() == User.UserRole.ADMIN;
        boolean isSalonCreator = salon.getCreateur().getId().equals(requestingUser.getId());
        
        if (!isGlobalAdmin && !isSalonCreator) {
            RoleSalon requestingRoleSalon = roleSalonRepository.findBySalonAndUtilisateur(salon, requestingUser)
                    .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de ce salon"));
            
            if (requestingRoleSalon.getRole() != SalonRole.ADMIN) {
                throw new UnauthorizedException("Seuls les administrateurs peuvent modifier les rôles");
            }
        }
        
        // Ne pas permettre de changer le rôle du créateur du salon
        if (salon.getCreateur().getId().equals(targetUser.getId()) && newRole != SalonRole.ADMIN) {
            throw new UnauthorizedException("Impossible de modifier le rôle du créateur du salon");
        }
        
        // Mettre à jour le rôle
        targetRoleSalon.setRole(newRole);
        RoleSalon updatedRoleSalon = roleSalonRepository.save(targetRoleSalon);
        
        MemberDto memberDto = mapToMemberDto(updatedRoleSalon);
        
        // Notifier les membres du salon de la modification du rôle
        messagingTemplate.convertAndSend("/topic/salon/" + salon.getId() + "/members/update", memberDto);
        
        return memberDto;
    }
    
    @Transactional
    public void removeMemberFromSalon(UUID salonId, UUID userId, String requestingUserEmail) {
        User requestingUser = userRepository.findByEmail(requestingUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur demandeur non trouvé"));
        
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon non trouvé"));
        
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur cible non trouvé"));
        
        // Vérifier que l'utilisateur cible est membre du salon
        RoleSalon targetRoleSalon = roleSalonRepository.findBySalonAndUtilisateur(salon, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("Cet utilisateur n'est pas membre du salon"));
        
        // Un utilisateur peut se retirer lui-même
        boolean isSelfRemoval = requestingUser.getId().equals(targetUser.getId());
        
        if (!isSelfRemoval) {
            // Vérifier que l'utilisateur demandeur est admin ou modérateur du salon ou admin global
            boolean isGlobalAdmin = requestingUser.getRole() == User.UserRole.ADMIN;
            
            if (!isGlobalAdmin) {
                RoleSalon requestingRoleSalon = roleSalonRepository.findBySalonAndUtilisateur(salon, requestingUser)
                        .orElseThrow(() -> new UnauthorizedException("Vous n'êtes pas membre de ce salon"));
                
                if (requestingRoleSalon.getRole() != SalonRole.ADMIN && requestingRoleSalon.getRole() != SalonRole.MODERATEUR) {
                    throw new UnauthorizedException("Vous n'avez pas le droit de retirer des membres de ce salon");
                }
                
                // Un modérateur ne peut pas retirer un admin
                if (requestingRoleSalon.getRole() == SalonRole.MODERATEUR && targetRoleSalon.getRole() == SalonRole.ADMIN) {
                    throw new UnauthorizedException("Un modérateur ne peut pas retirer un administrateur");
                }
            }
            
            // Ne pas permettre de retirer le créateur du salon
            if (salon.getCreateur().getId().equals(targetUser.getId())) {
                throw new UnauthorizedException("Impossible de retirer le créateur du salon");
            }
        }
        
        // Supprimer le rôle
        roleSalonRepository.delete(targetRoleSalon);
        
        // Notifier les membres du salon du retrait d'un membre
        messagingTemplate.convertAndSend("/topic/salon/" + salon.getId() + "/members/remove", userId);
    }
    
    private MemberDto mapToMemberDto(RoleSalon roleSalon) {
        MemberDto dto = new MemberDto();
        dto.setId(roleSalon.getId());
        dto.setUserId(roleSalon.getUtilisateur().getId());
        dto.setNom(roleSalon.getUtilisateur().getNom());
        dto.setPrenom(roleSalon.getUtilisateur().getPrenom());
        dto.setEmail(roleSalon.getUtilisateur().getEmail());
        dto.setRole(roleSalon.getRole().toString());
        dto.setStatut(roleSalon.getUtilisateur().getStatut().toString());
        dto.setIsCreator(roleSalon.getSalon().getCreateur().getId().equals(roleSalon.getUtilisateur().getId()));
        return dto;
    }
}