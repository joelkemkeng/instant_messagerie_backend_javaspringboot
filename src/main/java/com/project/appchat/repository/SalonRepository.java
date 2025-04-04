package com.project.appchat.repository;

import com.project.appchat.model.Salon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalonRepository extends JpaRepository<Salon, UUID> {
    boolean existsByNom(String nom);
}