package com.project.appchat.repository;

import com.project.appchat.model.RoleSalon;
import com.project.appchat.model.Salon;
import com.project.appchat.model.SalonRole;
import com.project.appchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Dans RoleSalonRepository.java
public interface RoleSalonRepository extends JpaRepository<RoleSalon, UUID> {
 List<RoleSalon> findBySalon(Salon salon);
 List<RoleSalon> findByUtilisateur(User utilisateur);
 Optional<RoleSalon> findBySalonAndUtilisateur(Salon salon, User utilisateur);
 boolean existsBySalonAndUtilisateurAndRole(Salon salon, User utilisateur, SalonRole role);
 
 // Ajouter cette méthode qui est manquante :
 boolean existsBySalonAndUtilisateur(Salon salon, User utilisateur);
}