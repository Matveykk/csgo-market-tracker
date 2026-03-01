package com.csgotracker.repository;

import com.csgotracker.model.Skin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkinRepository extends JpaRepository<Skin, Long> {

    Optional<Skin> findByMarketHashName(String marketHashName);

    boolean existsByMarketHashName(String marketHashName);

    List<Skin> findByWeaponType(String weaponType);

    List<Skin> findByRarity(String rarity);

    @Query("SELECT s FROM Skin s WHERE LOWER(s.marketHashName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Skin> searchByName(String query);
}