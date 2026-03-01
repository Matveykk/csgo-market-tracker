package com.csgotracker.service;

import com.csgotracker.dto.CreateSkinRequest;
import com.csgotracker.dto.SearchResultDTO;
import com.csgotracker.dto.SkinDTO;
import com.csgotracker.model.Skin;
import com.csgotracker.repository.SkinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkinService {

    private final SkinRepository skinRepository;

    @Cacheable(value = "skins", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Transactional(readOnly = true)
    public Page<SkinDTO> getAllSkins(Pageable pageable) {
        return skinRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Cacheable(value = "skins", key = "#id")
    @Transactional(readOnly = true)
    public SkinDTO getSkinById(Long id) {
        Skin skin = skinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skin not found with id: " + id));
        return convertToDTO(skin);
    }

    @Cacheable(value = "skinSearch", key = "#query")
    @Transactional(readOnly = true)
    public SearchResultDTO searchSkins(String query) {
        List<SkinDTO> results = skinRepository.searchByName(query)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        String message = results.isEmpty()
                ? "No skins found matching query: " + query
                : results.size() + " skin(s) found";

        return new SearchResultDTO(results, results.size(), query, message);
    }

    @CacheEvict(value = {"skins", "skinSearch"}, allEntries = true)
    @Transactional
    public SkinDTO createSkin(CreateSkinRequest request) {
        if (skinRepository.existsByMarketHashName(request.getMarketHashName())) {
            throw new RuntimeException("Skin with this market hash name already exists");
        }

        Skin skin = new Skin();
        skin.setMarketHashName(request.getMarketHashName());
        skin.setWeaponType(request.getWeaponType());
        skin.setSkinName(request.getSkinName());
        skin.setWear(request.getWear());
        skin.setRarity(request.getRarity());
        skin.setImageUrl(request.getImageUrl());

        Skin savedSkin = skinRepository.save(skin);
        return convertToDTO(savedSkin);
    }

    @CacheEvict(value = {"skins", "skinSearch"}, allEntries = true)
    @Transactional
    public SkinDTO updateSkin(Long id, CreateSkinRequest request) {
        Skin skin = skinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skin not found with id: " + id));

        skin.setMarketHashName(request.getMarketHashName());
        skin.setWeaponType(request.getWeaponType());
        skin.setSkinName(request.getSkinName());
        skin.setWear(request.getWear());
        skin.setRarity(request.getRarity());
        skin.setImageUrl(request.getImageUrl());

        Skin updatedSkin = skinRepository.save(skin);
        return convertToDTO(updatedSkin);
    }

    @CacheEvict(value = {"skins", "skinSearch"}, allEntries = true)
    @Transactional
    public void deleteSkin(Long id) {
        if (!skinRepository.existsById(id)) {
            throw new RuntimeException("Skin not found with id: " + id);
        }
        skinRepository.deleteById(id);
    }

    private SkinDTO convertToDTO(Skin skin) {
        return new SkinDTO(
                skin.getId(),
                skin.getMarketHashName(),
                skin.getWeaponType(),
                skin.getSkinName(),
                skin.getWear(),
                skin.getRarity(),
                skin.getImageUrl(),
                skin.getCreatedAt(),
                skin.getUpdatedAt()
        );
    }
}