package com.project.appchat.service;

import com.project.appchat.model.Message;
import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import com.project.appchat.repository.MessageRepository;
import com.project.appchat.repository.SalonRepository;
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

    @Transactional
    public Message saveMessage(Message message) {
        message.setDateEnvoi(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesBySalon(UUID salonId) {
        return salonRepository.findById(salonId)
                .map(messageRepository::findBySalonOrderByDateEnvoiDesc)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));
    }

    @Transactional(readOnly = true)
    public List<Message> getPrivateMessages(User expediteur, User destinataire) {
        return messageRepository.findByExpediteurAndDestinataire(expediteur, destinataire);
    }

    @Transactional(readOnly = true)
    public List<Message> getRecentMessagesBySalon(UUID salonId, LocalDateTime depuis) {
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new RuntimeException("Salon non trouvé"));
        return messageRepository.findBySalonAndDateEnvoiAfter(salon, depuis);
    }
}
