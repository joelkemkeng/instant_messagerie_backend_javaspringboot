package com.project.appchat.service;

import com.project.appchat.model.User;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByNomContainingIgnoreCase(query)
                .orElseGet(() -> userRepository.findByPrenomContainingIgnoreCase(query)
                        .orElseGet(List::of));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(User user) {
        if (findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.UserRole.USER);
        return userRepository.save(user);
    }
}