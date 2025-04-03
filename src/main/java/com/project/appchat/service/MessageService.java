package com.project.appchat.service;

import com.project.appchat.model.Message;
import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import com.project.appchat.repository.MessageRepository;
import com.project.appchat.repository.SalonRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final SalonRepository salonRepository;
    private final UserRepository userRepository;

    @Transactional
    public Message saveMessage(Message message) {
        validateMessage(message);
        message.setDateEnvoi(LocalDateTime.now());
        return messageRepository.save(message);
    }

    private void validateMessage(Message message) {
        User sender = userRepository.findById(message.getExpediteur().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        message.setExpediteur(sender);

        if (message.getDestinataire() != null) {
            User recipient = userRepository.findById(message.getDestinataire().getId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
            message.setDestinataire(recipient);
        }

        if (message.getSalon() != null) {
            Salon room = salonRepository.findById(message.getSalon().getId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
            message.setSalon(room);
        }

        if (message.getSalon() != null && message.getDestinataire() != null) {
            throw new IllegalArgumentException("Invalid message type");
        }
        
        if (message.getDestinataire() != null && 
            message.getExpediteur().getId().equals(message.getDestinataire().getId())) {
            throw new IllegalArgumentException("Self-messaging not allowed");
        }
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesBySalon(UUID salonId) {
        return messageRepository.findBySalonIdOrderByDateEnvoiDesc(salonId);
    }

    @Transactional(readOnly = true)
    public List<Message> getPrivateMessagesBetweenUsers(UUID userId1, UUID userId2) {
        return messageRepository.findConversationBetweenUsers(userId1, userId2);
    }

    @Transactional(readOnly = true)
    public List<Message> getRecentMessagesBySalon(UUID salonId, LocalDateTime since) {
        return messageRepository.findBySalonIdAndDateEnvoiAfterOrderByDateEnvoiDesc(salonId, since);
    }
}