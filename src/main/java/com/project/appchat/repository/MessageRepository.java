package com.project.appchat.repository;

import com.project.appchat.model.Message;
import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySalonOrderByDateEnvoi(Salon salon);
    
    // Messages privés entre deux utilisateurs
    @Query("SELECT m FROM Message m WHERE m.estPrive = true AND " +
           "((m.expediteur = ?1 AND m.destinataire = ?2) OR " +
           "(m.expediteur = ?2 AND m.destinataire = ?1)) " +
           "ORDER BY m.dateEnvoi")
    List<Message> findPrivateMessagesBetweenUsers(User user1, User user2);
    
    // Conversations récentes d'un utilisateur (messages privés)
    @Query("SELECT DISTINCT " +
           "CASE WHEN m.expediteur = ?1 THEN m.destinataire ELSE m.expediteur END " +
           "FROM Message m " +
           "WHERE m.estPrive = true AND (m.expediteur = ?1 OR m.destinataire = ?1)")
    List<User> findRecentConversations(User user);
}