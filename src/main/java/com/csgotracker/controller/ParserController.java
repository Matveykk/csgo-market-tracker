package com.csgotracker.controller;

import com.csgotracker.dto.steam.SteamPriceResponse;
import com.csgotracker.scheduler.PriceUpdateScheduler;
import com.csgotracker.service.SteamMarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parser")
@RequiredArgsConstructor
@Tag(name = "Parser", description = "Steam Market parsing endpoints")
public class ParserController {

    private final SteamMarketService steamMarketService;
    private final PriceUpdateScheduler priceUpdateScheduler;

    @GetMapping("/fetch-price")
    @Operation(summary = "Fetch price from Steam", description = "Fetch current price for a skin from Steam Market (test endpoint)")
    public ResponseEntity<SteamPriceResponse> fetchPrice(
            @RequestParam String marketHashName
    ) {
        SteamPriceResponse response = steamMarketService.fetchSkinPrice(marketHashName)
                .block();

        if (response != null && response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/fetch-and-save")
    @Operation(summary = "Fetch and save price", description = "Fetch price from Steam Market and save to database")
    public ResponseEntity<String> fetchAndSave(
            @RequestParam String marketHashName
    ) {
        steamMarketService.fetchAndSavePrice(marketHashName);
        return ResponseEntity.ok("Price fetched and saved successfully");
    }

    @PostMapping("/update-all")
    @Operation(summary = "Update all popular skins", description = "Manually trigger price update for all popular skins")
    public ResponseEntity<String> updateAllPrices() {
        new Thread(() -> priceUpdateScheduler.manualUpdate()).start();
        return ResponseEntity.ok("Price update started in background. Check logs for progress.");
    }
}