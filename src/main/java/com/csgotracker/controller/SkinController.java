package com.csgotracker.controller;

import com.csgotracker.dto.CreateSkinRequest;
import com.csgotracker.dto.SearchResultDTO;
import com.csgotracker.dto.SkinDTO;
import com.csgotracker.service.SkinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skins")
@RequiredArgsConstructor
@Tag(name = "Skins", description = "API for managing CS:GO skins")
public class SkinController {

    private final SkinService skinService;

    @GetMapping
    @Operation(summary = "Get all skins", description = "Retrieve a paginated list of all CS:GO skins")
    public ResponseEntity<Page<SkinDTO>> getAllSkins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<SkinDTO> skins = skinService.getAllSkins(pageable);
        return ResponseEntity.ok(skins);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skin by ID", description = "Retrieve a specific skin by its ID")
    public ResponseEntity<SkinDTO> getSkinById(@PathVariable Long id) {
        SkinDTO skin = skinService.getSkinById(id);
        return ResponseEntity.ok(skin);
    }

    @GetMapping("/search")
    @Operation(summary = "Search skins", description = "Search skins by name (partial match)")
    public ResponseEntity<SearchResultDTO> searchSkins(
            @RequestParam String query
    ) {
        SearchResultDTO result = skinService.searchSkins(query);

        if (result.getTotalResults() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Create a new skin", description = "Add a new CS:GO skin to the database")
    public ResponseEntity<SkinDTO> createSkin(@Valid @RequestBody CreateSkinRequest request) {
        SkinDTO createdSkin = skinService.createSkin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSkin);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a skin", description = "Update an existing skin by ID")
    public ResponseEntity<SkinDTO> updateSkin(
            @PathVariable Long id,
            @Valid @RequestBody CreateSkinRequest request
    ) {
        SkinDTO updatedSkin = skinService.updateSkin(id, request);
        return ResponseEntity.ok(updatedSkin);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a skin", description = "Delete a skin by ID")
    public ResponseEntity<Void> deleteSkin(@PathVariable Long id) {
        skinService.deleteSkin(id);
        return ResponseEntity.noContent().build();
    }
}