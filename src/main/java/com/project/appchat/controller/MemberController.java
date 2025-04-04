package com.project.appchat.controller;

import com.project.appchat.dto.MemberDto;
import com.project.appchat.model.SalonRole;
import com.project.appchat.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salons/{salonId}/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    
    @GetMapping
    public ResponseEntity<List<MemberDto>> getSalonMembers(@PathVariable UUID salonId) {
        return ResponseEntity.ok(memberService.getSalonMembers(salonId));
    }
    
    @PostMapping("/{userId}")
    public ResponseEntity<MemberDto> addMemberToSalon(
            @PathVariable UUID salonId,
            @PathVariable UUID userId,
            Authentication authentication) {
        return ResponseEntity.ok(memberService.addMemberToSalon(salonId, userId, authentication.getName()));
    }
    
    @PutMapping("/{userId}/role")
    public ResponseEntity<MemberDto> updateMemberRole(
            @PathVariable UUID salonId,
            @PathVariable UUID userId,
            @RequestParam SalonRole role,
            Authentication authentication) {
        return ResponseEntity.ok(memberService.updateMemberRole(salonId, userId, role, authentication.getName()));
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMemberFromSalon(
            @PathVariable UUID salonId,
            @PathVariable UUID userId,
            Authentication authentication) {
        memberService.removeMemberFromSalon(salonId, userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}