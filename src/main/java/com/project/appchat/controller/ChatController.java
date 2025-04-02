package com.project.appchat.controller;

import com.project.appchat.model.ChatMessage;
import com.project.appchat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public ChatMessage sendMessage(@Payload @NonNull ChatMessage chatMessage) {
        logger.info("Message reçu de {} pour le salon {}", chatMessage.getSenderName(), chatMessage.getRoomId());
        return chatService.processMessage(chatMessage);
    }

    @MessageMapping("/chat.addUser")
    public ChatMessage addUser(@Payload @NonNull ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        logger.info("Ajout de l'utilisateur {} au salon {}", chatMessage.getSenderName(), chatMessage.getRoomId());
        
        // Vérifier que headerAccessor n'est pas null
        if (headerAccessor != null) {
            // Vérifier que getSessionAttributes() ne retourne pas null
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.put("username", chatMessage.getSenderName());
                if (chatMessage.getRoomId() != null) {
                    sessionAttributes.put("roomId", chatMessage.getRoomId());
                }
            } else {
                logger.warn("Impossible d'accéder aux attributs de session pour {}", chatMessage.getSenderName());
            }
        } else {
            logger.warn("HeaderAccessor est null pour {}", chatMessage.getSenderName());
        }
        
        return chatService.processMessage(chatMessage);
    }

    @MessageMapping("/chat.private")
    public ChatMessage sendPrivateMessage(@Payload @NonNull ChatMessage chatMessage) {
        logger.info("Message privé de {} à {}", chatMessage.getSenderName(), chatMessage.getRecipientId());
        return chatService.processPrivateMessage(chatMessage);
    }
    
    @MessageMapping("/chat.removeUser")
    public ChatMessage removeUser(@Payload @NonNull ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        logger.info("Déconnexion de l'utilisateur {}", chatMessage.getSenderName());
        
        // Vérifier que headerAccessor n'est pas null
        if (headerAccessor != null) {
            // Vérifier que getSessionAttributes() ne retourne pas null
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.remove("username");
                sessionAttributes.remove("roomId");
            } else {
                logger.warn("Impossible d'accéder aux attributs de session pour {}", chatMessage.getSenderName());
            }
        } else {
            logger.warn("HeaderAccessor est null pour {}", chatMessage.getSenderName());
        }
        
        return chatService.processMessage(chatMessage);
    }
}
