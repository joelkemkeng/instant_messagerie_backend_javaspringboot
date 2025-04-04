package com.project.appchat.service;

import com.project.appchat.dto.UserDto;
import com.project.appchat.exception.ResourceNotFoundException;
import com.project.appchat.model.User;
import com.project.appchat.repository.MessageRepository;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return mapToDto(user);
    }
    
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return mapToDto(user);
    }
    
    public List<UserDto> getRecentContacts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
                
        List<User> contacts = messageRepository.findRecentConversations(user);
        
        return contacts.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setStatut(user.getStatut().toString());
        dto.setRole(user.getRole().toString());
        return dto;
    }
}