package com.csgotracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSkinRequest {

    @NotBlank(message = "Market hash name is required")
    @Size(max = 255, message = "Market hash name must be less than 255 characters")
    private String marketHashName;

    @Size(max = 100, message = "Weapon type must be less than 100 characters")
    private String weaponType;

    @Size(max = 100, message = "Skin name must be less than 100 characters")
    private String skinName;

    @Size(max = 50, message = "Wear must be less than 50 characters")
    private String wear;

    @Size(max = 50, message = "Rarity must be less than 50 characters")
    private String rarity;

    private String imageUrl;
}