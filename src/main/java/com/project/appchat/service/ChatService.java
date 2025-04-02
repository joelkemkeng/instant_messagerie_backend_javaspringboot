package com.project.appchat.service;

import com.project.appchat.model.ChatMessage;
import com.project.appchat.model.Message;
import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import com.project.appchat.repository.SalonRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final SalonRepository salonRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessage processMessage(ChatMessage chatMessage) {
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Sauvegarder le message dans la base de données
        if (chatMessage.getRoomId() != null) {
            User sender = userRepository.findByNomUtilisateur(chatMessage.getSenderName())
                .orElseThrow(() -> new RuntimeException("Utilisateur expéditeur non trouvé"));
            
            Salon salon = salonRepository.findById(UUID.fromString(chatMessage.getRoomId()))
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));
            
            Message message = Message.builder()
                .contenu(chatMessage.getContent())
                .dateEnvoi(chatMessage.getTimestamp())
                .expediteur(sender)
                .salon(salon)
                .build();
            
            messageService.saveMessage(message);
        }
        
        // Broadcast to all users in the room
        messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), chatMessage);
        return chatMessage;
    }

    @Transactional
    public ChatMessage processPrivateMessage(ChatMessage chatMessage) {
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setType(ChatMessage.MessageType.PRIVATE);
        
        // Sauvegarder le message privé
        User sender = userRepository.findByNomUtilisateur(chatMessage.getSenderName())
            .orElseThrow(() -> new RuntimeException("Utilisateur expéditeur non trouvé"));
        
        User recipient = userRepository.findByNomUtilisateur(chatMessage.getRecipientId())
            .orElseThrow(() -> new RuntimeException("Utilisateur destinataire non trouvé"));
        
        Message message = Message.builder()
            .contenu(chatMessage.getContent())
            .dateEnvoi(chatMessage.getTimestamp())
            .expediteur(sender)
            .destinataire(recipient)
            .build();
        
        messageService.saveMessage(message);
        
        // Send to specific user
        messagingTemplate.convertAndSendToUser(
            chatMessage.getRecipientId(),
            "/topic/private-messages",
            chatMessage
        );
        
        // Send confirmation to sender
        messagingTemplate.convertAndSendToUser(
            chatMessage.getSenderId(),
            "/topic/private-messages",
            chatMessage
        );
        
        return chatMessage;
    }
}
