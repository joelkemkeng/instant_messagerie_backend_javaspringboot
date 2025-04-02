package com.project.appchat.controller;

import com.project.appchat.model.Salon;
import com.project.appchat.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    public ResponseEntity<List<Salon>> getAllSalons() {
        return ResponseEntity.ok(salonService.getAllSalons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Salon> getSalonById(@PathVariable UUID id) {
        return salonService.getSalonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Salon> createSalon(@RequestBody Map<String, String> request) {
        String nom = request.get("nom");
        String description = request.get("description");
        String creatorUsername = request.get("creatorUsername");

        if (nom == null || creatorUsername == null) {
            return ResponseEntity.badRequest().build();
        }

        Salon salon = salonService.createSalon(nom, description, creatorUsername);
        return ResponseEntity.ok(salon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalon(@PathVariable UUID id, @RequestParam String username) {
        try {
            salonService.deleteSalon(id, username);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
