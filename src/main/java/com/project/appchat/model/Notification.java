package com.project.appchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;
    
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeNotification type;
    
    @Column(name = "date_notification")
    private LocalDateTime dateNotification = LocalDateTime.now();
    
    @Column(name = "lu")
    private boolean lu = false;
    
    public enum TypeNotification {
        NOUVEAU_MESSAGE, INVITATION_SALON, SANCTION
    }
}
