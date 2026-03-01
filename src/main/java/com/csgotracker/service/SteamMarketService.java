package com.csgotracker.service;

import com.csgotracker.dto.steam.SteamPriceResponse;
import com.csgotracker.model.PriceHistory;
import com.csgotracker.model.Skin;
import com.csgotracker.repository.PriceHistoryRepository;
import com.csgotracker.repository.SkinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamMarketService {

    private final WebClient steamWebClient;
    private final SkinRepository skinRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    private static final String PRICE_OVERVIEW_PATH = "/market/priceoverview/";
    private static final int CSGO_APP_ID = 730;
    private static final int USD_CURRENCY = 1;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration RATE_LIMIT_DELAY = Duration.ofSeconds(2);

    public Mono<SteamPriceResponse> fetchSkinPrice(String marketHashName) {
        String uri = UriComponentsBuilder.fromPath(PRICE_OVERVIEW_PATH)
                .queryParam("appid", CSGO_APP_ID)
                .queryParam("currency", USD_CURRENCY)
                .queryParam("market_hash_name", marketHashName)
                .build()
                .toUriString();

        log.info("Fetching price for: {}", marketHashName);

        return steamWebClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(SteamPriceResponse.class)
                .timeout(REQUEST_TIMEOUT)
                .doOnSuccess(response -> log.info("Successfully fetched price for: {}", marketHashName))
                .doOnError(error -> log.error("Error fetching price for {}: {}", marketHashName, error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Failed to fetch price, returning empty response", error);
                    return Mono.empty();
                });
    }

    @Transactional
    public void fetchAndSavePrice(String marketHashName) {
        try {
            Skin skin = findOrCreateSkin(marketHashName);

            SteamPriceResponse priceResponse = fetchSkinPrice(marketHashName)
                    .delayElement(RATE_LIMIT_DELAY)
                    .block();

            if (priceResponse != null && priceResponse.isSuccess() && priceResponse.getLowestPrice() != null) {
                BigDecimal price = parsePrice(priceResponse.getLowestPrice());
                Integer volume = parseVolume(priceResponse.getVolume());

                PriceHistory priceHistory = new PriceHistory();
                priceHistory.setSkin(skin);
                priceHistory.setPrice(price);
                priceHistory.setVolume(volume);
                priceHistory.setSource("Steam Market");

                priceHistoryRepository.save(priceHistory);

                log.info("Saved price for {}: ${} (volume: {})", marketHashName, price, volume);
            } else {
                log.warn("No price data available for: {}", marketHashName);
            }

        } catch (Exception e) {
            log.error("Error processing skin: {}", marketHashName, e);
        }
    }

    private Skin findOrCreateSkin(String marketHashName) {
        Optional<Skin> existingSkin = skinRepository.findByMarketHashName(marketHashName);

        if (existingSkin.isPresent()) {
            return existingSkin.get();
        }

        Skin newSkin = new Skin();
        newSkin.setMarketHashName(marketHashName);
        parseSkinName(marketHashName, newSkin);

        return skinRepository.save(newSkin);
    }

    private void parseSkinName(String marketHashName, Skin skin) {
        try {
            String[] parts = marketHashName.split("\\|");

            if (parts.length >= 2) {
                skin.setWeaponType(parts[0].trim());

                String secondPart = parts[1].trim();
                if (secondPart.contains("(") && secondPart.contains(")")) {
                    int wearStart = secondPart.lastIndexOf("(");
                    skin.setSkinName(secondPart.substring(0, wearStart).trim());
                    skin.setWear(secondPart.substring(wearStart + 1, secondPart.length() - 1).trim());
                } else {
                    skin.setSkinName(secondPart);
                }
            } else {
                // Если формат не стандартный, сохраняем всё как есть
                skin.setWeaponType("Unknown");
                skin.setSkinName(marketHashName);
            }
        } catch (Exception e) {
            log.warn("Failed to parse skin name: {}", marketHashName, e);
            skin.setWeaponType("Unknown");
            skin.setSkinName(marketHashName);
        }
    }

    private BigDecimal parsePrice(String priceString) {
        try {
            String cleaned = priceString.replaceAll("[^0-9.]", "");
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            log.error("Failed to parse price: {}", priceString, e);
            return BigDecimal.ZERO;
        }
    }

    private Integer parseVolume(String volumeString) {
        try {
            if (volumeString == null || volumeString.isEmpty()) {
                return 0;
            }
            String cleaned = volumeString.replaceAll("[^0-9]", "");
            return Integer.parseInt(cleaned);
        } catch (Exception e) {
            log.error("Failed to parse volume: {}", volumeString, e);
            return 0;
        }
    }
}