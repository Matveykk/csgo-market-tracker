package com.csgotracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "skins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market_hash_name", unique = true, nullable = false, length = 255)
    private String marketHashName;

    @Column(name = "weapon_type", length = 100)
    private String weaponType;

    @Column(name = "skin_name", length = 100)
    private String skinName;

    @Column(name = "wear", length = 50)
    private String wear;

    @Column(name = "rarity", length = 50)
    private String rarity;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}