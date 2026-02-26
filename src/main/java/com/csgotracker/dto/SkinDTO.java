package com.csgotracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkinDTO {
    private Long id;
    private String marketHashName;
    private String weaponType;
    private String skinName;
    private String wear;
    private String rarity;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}