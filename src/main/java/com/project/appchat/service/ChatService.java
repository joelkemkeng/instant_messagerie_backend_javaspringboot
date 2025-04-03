package com.project.appchat.service;

import com.project.appchat.model.ChatMessage;
import com.project.appchat.model.Message;
import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import com.project.appchat.repository.SalonRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String ROOM_TOPIC = "/topic/room/";
    private static final String PRIVATE_QUEUE = "/queue/private";

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final SalonRepository salonRepository;
    private final UserRepository userRepository;

    public ChatMessage processMessage(ChatMessage chatMessage) {
        User currentUser = getCurrentUser();
        Salon salon = getSalon(chatMessage.getRoomId());
        validateRoomMembership(currentUser, salon);

        Message message = buildMessage(chatMessage, currentUser, salon);
        Message savedMessage = messageService.saveMessage(message);
        
        ChatMessage response = buildResponse(savedMessage, currentUser, chatMessage.getRoomId());
        sendToRoom(chatMessage.getRoomId(), response);
        
        return response;
    }

    public ChatMessage processPrivateMessage(ChatMessage chatMessage) {
        User currentUser = getCurrentUser();
        User recipient = getUser(chatMessage.getRecipientId());
        validateSelfMessaging(currentUser, recipient);

        Message message = buildPrivateMessage(chatMessage, currentUser, recipient);
        Message savedMessage = messageService.saveMessage(message);

        ChatMessage response = buildPrivateResponse(savedMessage, currentUser, recipient);
        sendPrivateMessage(response, currentUser.getId().toString());
        
        return response;
    }

    // Helper methods
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private User getUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Salon getSalon(String roomId) {
        return salonRepository.findById(UUID.fromString(roomId))
            .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    private void validateRoomMembership(User user, Salon salon) {
        if (!salon.getUsers().contains(user)) {
            throw new RuntimeException("Room access denied") ;
        }
    }

    private void validateSelfMessaging(User sender, User recipient) {
        if (sender.getId().equals(recipient.getId())) {
            throw new RuntimeException("Self-messaging not allowed");
        }
    }

    private Message buildMessage(ChatMessage chatMessage, User user, Salon salon) {
        return Message.builder()
            .contenu(chatMessage.getContent())
            .expediteur(user)
            .salon(salon)
            .build();
    }

    private Message buildPrivateMessage(ChatMessage chatMessage, User sender, User recipient) {
        return Message.builder()
            .contenu(chatMessage.getContent())
            .expediteur(sender)
            .destinataire(recipient)
            .build();
    }

    private ChatMessage buildResponse(Message message, User user, String roomId) {
        return ChatMessage.builder()
            .id(message.getId().toString())
            .content(message.getContenu())
            .senderId(user.getId().toString())
            .senderName(user.getPrenom() + " " + user.getNom())
            .roomId(roomId)
            .timestamp(message.getDateEnvoi())
            .build();
    }

    private ChatMessage buildPrivateResponse(Message message, User sender, User recipient) {
        return ChatMessage.builder()
            .id(message.getId().toString())
            .content(message.getContenu())
            .senderId(sender.getId().toString())
            .senderName(sender.getPrenom() + " " + sender.getNom())
            .recipientId(recipient.getId().toString())
            .timestamp(message.getDateEnvoi())
            .build();
    }

    private void sendToRoom(String roomId, ChatMessage message) {
        messagingTemplate.convertAndSend(ROOM_TOPIC + roomId, message);
    }

    private void sendPrivateMessage(ChatMessage message, String senderId) {
        // To recipient
        messagingTemplate.convertAndSendToUser(
            message.getRecipientId(), 
            PRIVATE_QUEUE, 
            message
        );
        // To sender
        messagingTemplate.convertAndSendToUser(
            senderId,
            PRIVATE_QUEUE,
            message
        );
    }
}