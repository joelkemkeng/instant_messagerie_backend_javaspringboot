package com.project.appchat.service;

import com.project.appchat.dto.MessageDto;
import com.project.appchat.exception.ResourceNotFoundException;
import com.project.appchat.exception.UnauthorizedException;
import com.project.appchat.model.Message;
import com.project.appchat.model.RoleSalon;
import com.project.appchat.model.Salon;
import com.project.appchat.model.SalonRole;
import com.project.appchat.model.User;
import com.project.appchat.repository.MessageRepository;
import com.project.appchat.repository.RoleSalonRepository;
import com.project.appchat.repository.SalonRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SalonRepository salonRepository;
    private final RoleSalonRepository roleSalonRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageDto sendMessageToSalon(MessageDto messageDto, String expediteurEmail) {
        User expediteur = userRepository.findByEmail(expediteurEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Salon salon = salonRepository.findById(messageDto.getSalonId())
                .orElseThrow(() -> new ResourceNotFoundException("Salon non trouvé"));
        
        // Vérifier que l'utilisateur est membre du salon
        if (!roleSalonRepository.existsBySalonAndUtilisateur(salon, expediteur)) {
            throw new UnauthorizedException("Vous n'êtes pas membre de ce salon");
        }
        
        Message message = new Message();
        message.setContenu(messageDto.getContenu());
        message.setDateEnvoi(LocalDateTime.now());
        message.setExpediteur(expediteur);
        message.setSalon(salon);
        message.setEstPrive(false);
        
        Message savedMessage = messageRepository.save(message);
        
        MessageDto savedDto = mapToDto(savedMessage);
        
        // Envoyer le message via WebSocket
        messagingTemplate.convertAndSend("/topic/salon/" + salon.getId(), savedDto);
        
        return savedDto;
    }
    
    @Transactional
    public MessageDto sendPrivateMessage(MessageDto messageDto, String expediteurEmail) {
        User expediteur = userRepository.findByEmail(expediteurEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Expéditeur non trouvé"));
        
        User destinataire = userRepository.findById(messageDto.getDestinataireId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire non trouvé"));
        
        Message message = new Message();
        message.setContenu(messageDto.getContenu());
        message.setDateEnvoi(LocalDateTime.now());
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setEstPrive(true);
        
        Message savedMessage = messageRepository.save(message);
        
        MessageDto savedDto = mapToDto(savedMessage);
        
        // Envoyer le message via WebSocket au destinataire
        messagingTemplate.convertAndSendToUser(
                destinataire.getEmail(),
                "/queue/messages",
                savedDto
        );
        
        return savedDto;
    }
    
    public List<MessageDto> getSalonMessages(UUID salonId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new ResourceNotFoundException("Salon non trouvé"));
        
        // Vérifier que l'utilisateur est membre du salon
        if (!roleSalonRepository.existsBySalonAndUtilisateur(salon, user)) {
            throw new UnauthorizedException("Vous n'êtes pas membre de ce salon");
        }
        
        return messageRepository.findBySalonOrderByDateEnvoi(salon).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<MessageDto> getPrivateMessages(UUID otherUserId, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        return messageRepository.findPrivateMessagesBetweenUsers(currentUser, otherUser).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteMessage(UUID messageId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message non trouvé"));
        
        // Vérifier si l'utilisateur est l'expéditeur du message
        boolean isAuthor = message.getExpediteur().getId().equals(user.getId());
        
        // Vérifier si l'utilisateur est admin global
        boolean isGlobalAdmin = user.getRole() == User.UserRole.ADMIN;
        
        // Vérifier si l'utilisateur est modérateur ou admin du salon (pour les messages de salon)
        boolean isSalonAdmin = false;
        if (message.getSalon() != null) {
            RoleSalon roleSalon = roleSalonRepository.findBySalonAndUtilisateur(message.getSalon(), user).orElse(null);
            isSalonAdmin = roleSalon != null && 
                          (roleSalon.getRole() == SalonRole.ADMIN || roleSalon.getRole() == SalonRole.MODERATEUR);
        }
        
        if (!isAuthor && !isGlobalAdmin && !isSalonAdmin) {
            throw new UnauthorizedException("Vous n'avez pas l'autorisation de supprimer ce message");
        }
        
        messageRepository.delete(message);
        
        // Notifier les clients via WebSocket de la suppression
        if (message.getSalon() != null) {
            messagingTemplate.convertAndSend("/topic/salon/" + message.getSalon().getId() + "/delete", messageId);
        } else if (message.getDestinataire() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getDestinataire().getEmail(),
                    "/queue/messages/delete",
                    messageId
            );
            messagingTemplate.convertAndSendToUser(
                    message.getExpediteur().getEmail(),
                    "/queue/messages/delete",
                    messageId
            );
        }
    }
    
    private MessageDto mapToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContenu(message.getContenu());
        dto.setDateEnvoi(message.getDateEnvoi());
        dto.setExpediteurId(message.getExpediteur().getId());
        dto.setExpediteurNom(message.getExpediteur().getPrenom() + " " + message.getExpediteur().getNom());
        dto.setEstPrive(message.getEstPrive());
        
        if (message.getSalon() != null) {
            dto.setSalonId(message.getSalon().getId());
        }
        
        if (message.getDestinataire() != null) {
            dto.setDestinataireId(message.getDestinataire().getId());
            dto.setDestinataireNom(message.getDestinataire().getPrenom() + " " + message.getDestinataire().getNom());
        }
        
        return dto;
    }
}