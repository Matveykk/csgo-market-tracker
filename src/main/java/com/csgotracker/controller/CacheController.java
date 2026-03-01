package com.csgotracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/cache")
@RequiredArgsConstructor
@Tag(name = "Cache", description = "Cache management endpoints")
public class CacheController {

    private final CacheManager cacheManager;

    @DeleteMapping("/clear")
    @Operation(summary = "Clear all caches", description = "Clear all Redis caches")
    public ResponseEntity<String> clearAllCaches() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(cacheName ->
                Objects.requireNonNull(cacheManager.getCache(cacheName)).clear()
        );
        return ResponseEntity.ok("All caches cleared. Cache names: " + cacheNames);
    }

    @DeleteMapping("/clear/{cacheName}")
    @Operation(summary = "Clear specific cache", description = "Clear a specific Redis cache by name")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok("Cache '" + cacheName + "' cleared");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/names")
    @Operation(summary = "Get cache names", description = "Get all available cache names")
    public ResponseEntity<Collection<String>> getCacheNames() {
        return ResponseEntity.ok(cacheManager.getCacheNames());
    }
}