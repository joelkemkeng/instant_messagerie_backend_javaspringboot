package com.project.appchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDto {
    private String userId;
    private String status; // EN_LIGNE, HORS_LIGNE
}