package com.project.appchat.service;

import com.project.appchat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessage processMessage(ChatMessage chatMessage) {
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        
        // Broadcast to all users in the room
        messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), chatMessage);
        return chatMessage;
    }

    public ChatMessage processPrivateMessage(ChatMessage chatMessage) {
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setType(ChatMessage.MessageType.PRIVATE);
        
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
