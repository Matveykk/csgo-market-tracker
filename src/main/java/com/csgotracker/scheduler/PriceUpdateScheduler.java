package com.csgotracker.scheduler;

import com.csgotracker.service.SteamMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceUpdateScheduler {

    private final SteamMarketService steamMarketService;

    private static final List<String> POPULAR_SKINS = Arrays.asList(
            "AK-47 | Redline (Field-Tested)",
            "AWP | Asiimov (Field-Tested)",
            "M4A4 | Howl (Field-Tested)",
            "AWP | Dragon Lore (Factory New)",
            "Karambit | Fade (Factory New)",
            "M4A1-S | Hyper Beast (Field-Tested)",
            "AK-47 | Fire Serpent (Field-Tested)",
            "Glock-18 | Fade (Factory New)",
            "Desert Eagle | Blaze (Factory New)",
            "USP-S | Kill Confirmed (Field-Tested)"
    );

    @Scheduled(cron = "0 0 */6 * * *")
    public void updatePrices() {
        log.info("=== Starting scheduled price update ===");
        log.info("Total skins to update: {}", POPULAR_SKINS.size());

        int successCount = 0;
        int failedCount = 0;

        for (String skinName : POPULAR_SKINS) {
            try {
                log.info("Processing: {}", skinName);
                steamMarketService.fetchAndSavePrice(skinName);
                successCount++;

                Thread.sleep(2000);

            } catch (Exception e) {
                log.error("Failed to update price for: {}", skinName, e);
                failedCount++;
            }
        }

        log.info("=== Price update completed ===");
        log.info("Success: {}, Failed: {}", successCount, failedCount);
    }

    public void manualUpdate() {
        log.info("Manual price update triggered");
        updatePrices();
    }
}