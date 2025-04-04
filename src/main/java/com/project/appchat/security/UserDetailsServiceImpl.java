package com.project.appchat.security;

import com.project.appchat.model.User;
import com.project.appchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
public UserDetails loadUserByUsername(String email) {
User user = userRepository.findByEmail(email)
    .orElseThrow(() -> new UsernameNotFoundException("Email non trouvé: " + email));

return org.springframework.security.core.userdetails.User
    .withUsername(user.getEmail())
    .password(user.getPassword())
    .authorities("ROLE_" + user.getRole().name())
    .build();
}
}