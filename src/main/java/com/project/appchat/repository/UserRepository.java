package com.project.appchat.repository;

import com.project.appchat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<List<User>> findByNomContainingIgnoreCase(String nom);
    Optional<List<User>> findByPrenomContainingIgnoreCase(String prenom);
}