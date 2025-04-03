package com.project.appchat.controller;

import com.project.appchat.model.Salon;
import com.project.appchat.service.SalonService;
import com.project.appchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salons")
public class SalonController {

    @Autowired
    private SalonService salonService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Salon>> getAllSalons() {
        return ResponseEntity.ok(salonService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Salon> createSalon(@RequestBody Salon salon) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        salon.setCreateur(userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé")));
        return ResponseEntity.ok(salonService.save(salon));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Salon> getSalonById(@PathVariable UUID id) {
        return ResponseEntity.ok(salonService.findById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Salon>> searchSalons(@RequestParam String query) {
        return ResponseEntity.ok(salonService.searchSalons(query));
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> joinSalon(@PathVariable UUID id) {
        salonService.joinSalon(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> leaveSalon(@PathVariable UUID id) {
        salonService.leaveSalon(id);
        return ResponseEntity.ok().build();
    }
}