package com.project.appchat.controller;

import com.project.appchat.dto.SalonDto;
import com.project.appchat.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    public ResponseEntity<List<SalonDto>> getAllSalons() {
        return ResponseEntity.ok(salonService.getAllSalons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalonDto> getSalonById(@PathVariable UUID id) {
        return ResponseEntity.ok(salonService.getSalonById(id));
    }

    @PostMapping
    public ResponseEntity<SalonDto> createSalon(
            @Valid @RequestBody SalonDto salonDto,
            Authentication authentication) {
        return ResponseEntity.ok(salonService.createSalon(salonDto, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalon(
            @PathVariable UUID id,
            Authentication authentication) {
        salonService.deleteSalon(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}