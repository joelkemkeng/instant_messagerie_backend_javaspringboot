package com.project.appchat.repository;

import com.project.appchat.model.Message;
import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySalonOrderByDateEnvoiDesc(Salon salon);
    List<Message> findByExpediteurAndDestinataire(User expediteur, User destinataire);
    List<Message> findBySalonAndDateEnvoiAfter(Salon salon, java.time.LocalDateTime date);
}
