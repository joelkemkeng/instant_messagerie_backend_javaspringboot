package com.project.appchat.repository;

import com.project.appchat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    @Query("SELECT m FROM Message m WHERE m.salon.id = :salonId ORDER BY m.dateEnvoi DESC")
    List<Message> findBySalonIdOrderByDateEnvoiDesc(@Param("salonId") UUID salonId);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.expediteur.id = :user1Id AND m.destinataire.id = :user2Id) OR " +
           "(m.expediteur.id = :user2Id AND m.destinataire.id = :user1Id) " +
           "ORDER BY m.dateEnvoi DESC")
    List<Message> findConversationBetweenUsers(@Param("user1Id") UUID user1Id, 
                                             @Param("user2Id") UUID user2Id);
    
    @Query("SELECT m FROM Message m WHERE m.salon.id = :salonId AND m.dateEnvoi > :date ORDER BY m.dateEnvoi DESC")
    List<Message> findBySalonIdAndDateEnvoiAfterOrderByDateEnvoiDesc(
        @Param("salonId") UUID salonId,
        @Param("date") LocalDateTime date
    );
}