package com.project.appchat.service;

import com.project.appchat.model.ChatMessage;
import com.project.appchat.model.Message;
import com.project.appchat.model.Salon;
import com.project.appchat.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.project.appchat.model.User;
import com.project.appchat.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final SalonRepository salonRepository;
    private final UserRepository userRepository;

    public ChatMessage processMessage(ChatMessage chatMessage) {
    String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
    User currentUser = userRepository.findById(UUID.fromString(currentUserId))
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    
    // Vérifier si l'utilisateur fait partie du salon
    Salon salon = salonRepository.findById(UUID.fromString(chatMessage.getRoomId()))
        .orElseThrow(() -> new RuntimeException("Salon non trouvé"));
    
    if (!salon.getUsers().contains(currentUser)) {
        throw new RuntimeException("Vous n'êtes pas membre de ce salon");
    }
    
    Message message = Message.builder()
        .contenu(chatMessage.getContent())
        .expediteur(currentUser)
        .dateEnvoi(chatMessage.getTimestamp())
        .salon(salon)
        .build();
    
    Message savedMessage = messageService.saveMessage(message);
    
    ChatMessage response = ChatMessage.builder()
        .id(savedMessage.getId().toString())
        .content(savedMessage.getContenu())
        .senderId(currentUserId)
        .senderName(currentUser.getNom() + " " + currentUser.getPrenom())
        .roomId(chatMessage.getRoomId())
        .type(ChatMessage.MessageType.CHAT)
        .timestamp(savedMessage.getDateEnvoi())
        .build();
    
    messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), response);
    return response;
}
    public ChatMessage processPrivateMessage(ChatMessage chatMessage) {
        // Récupérer l'utilisateur connecté
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findById(UUID.fromString(currentUserId))
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Créer un nouveau message privé
        Message message = Message.builder()
            .contenu(chatMessage.getContent())
            .expediteur(currentUser)  // Utiliser l'objet User au lieu de l'ID
            .dateEnvoi(chatMessage.getTimestamp())
            .build();
        
        // Sauvegarder le message
        Message savedMessage = messageService.saveMessage(message);
        
        // Créer une réponse ChatMessage
        ChatMessage response = ChatMessage.builder()
            .id(savedMessage.getId().toString())
            .content(savedMessage.getContenu())
            .senderId(currentUserId)
            .senderName(currentUserId) // À ajuster selon votre modèle User
            .recipientId(chatMessage.getRecipientId())
            .type(ChatMessage.MessageType.PRIVATE)
            .timestamp(savedMessage.getDateEnvoi())
            .build();
        
        // Send to specific user
        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipientId(),
            "/topic/private-messages",
            response
        );
        
        // Send confirmation to sender
        messagingTemplate.convertAndSendToUser(
            currentUserId,
            "/topic/private-messages",
            response
        );
        
        return response;
    }
}