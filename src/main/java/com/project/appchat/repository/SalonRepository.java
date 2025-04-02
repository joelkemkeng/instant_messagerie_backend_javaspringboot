package com.project.appchat.repository;

import com.project.appchat.model.Salon;
import com.project.appchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalonRepository extends JpaRepository<Salon, UUID> {
    Optional<Salon> findByNom(String nom);
    List<Salon> findByCreateur(User createur);
    boolean existsByNom(String nom);
}
