package com.csgotracker.controller;

import com.csgotracker.dto.AddPriceRequest;
import com.csgotracker.dto.PriceDTO;
import com.csgotracker.dto.PriceStatsDTO;
import com.csgotracker.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "API for managing price history")
public class PriceController {

    private final PriceService priceService;

    @PostMapping
    @Operation(summary = "Add price", description = "Add a new price entry for a skin")
    public ResponseEntity<PriceDTO> addPrice(@Valid @RequestBody AddPriceRequest request) {
        PriceDTO price = priceService.addPrice(
                request.getSkinId(),
                request.getPrice(),
                request.getVolume(),
                request.getSource()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(price);
    }

    @GetMapping("/skin/{skinId}")
    @Operation(summary = "Get price history", description = "Get all price history for a specific skin")
    public ResponseEntity<List<PriceDTO>> getPriceHistory(@PathVariable Long skinId) {
        List<PriceDTO> history = priceService.getPriceHistory(skinId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/skin/{skinId}/range")
    @Operation(summary = "Get price history by date range", description = "Get price history for a skin within a specific date range")
    public ResponseEntity<List<PriceDTO>> getPriceHistoryByDateRange(
            @PathVariable Long skinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<PriceDTO> history = priceService.getPriceHistoryByDateRange(skinId, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/skin/{skinId}/current")
    @Operation(summary = "Get current price", description = "Get the most recent price for a skin")
    public ResponseEntity<PriceDTO> getCurrentPrice(@PathVariable Long skinId) {
        PriceDTO price = priceService.getCurrentPrice(skinId);
        return ResponseEntity.ok(price);
    }

    @GetMapping("/skin/{skinId}/stats")
    @Operation(summary = "Get price statistics", description = "Get price statistics (min, max, avg) for a skin within a date range")
    public ResponseEntity<PriceStatsDTO> getPriceStats(
            @PathVariable Long skinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        PriceStatsDTO stats = priceService.getPriceStats(skinId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
}