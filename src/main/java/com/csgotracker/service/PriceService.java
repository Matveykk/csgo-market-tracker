package com.csgotracker.service;

import com.csgotracker.dto.PriceDTO;
import com.csgotracker.dto.PriceStatsDTO;
import com.csgotracker.model.PriceHistory;
import com.csgotracker.model.Skin;
import com.csgotracker.repository.PriceHistoryRepository;
import com.csgotracker.repository.SkinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final SkinRepository skinRepository;

    @CacheEvict(value = {"currentPrices", "priceHistory", "priceStats"}, key = "#skinId")
    @Transactional
    public PriceDTO addPrice(Long skinId, BigDecimal price, Integer volume, String source) {
        Skin skin = skinRepository.findById(skinId)
                .orElseThrow(() -> new RuntimeException("Skin not found with id: " + skinId));

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setSkin(skin);
        priceHistory.setPrice(price);
        priceHistory.setVolume(volume);
        priceHistory.setSource(source != null ? source : "Manual");

        PriceHistory saved = priceHistoryRepository.save(priceHistory);
        return convertToDTO(saved);
    }

    @Cacheable(value = "priceHistory", key = "#skinId")
    @Transactional(readOnly = true)
    public List<PriceDTO> getPriceHistory(Long skinId) {
        if (!skinRepository.existsById(skinId)) {
            throw new RuntimeException("Skin not found with id: " + skinId);
        }

        return priceHistoryRepository.findBySkinIdOrderByRecordedAtDesc(skinId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "priceHistory", key = "#skinId + '-' + #startDate + '-' + #endDate")
    @Transactional(readOnly = true)
    public List<PriceDTO> getPriceHistoryByDateRange(Long skinId, LocalDateTime startDate, LocalDateTime endDate) {
        if (!skinRepository.existsById(skinId)) {
            throw new RuntimeException("Skin not found with id: " + skinId);
        }

        return priceHistoryRepository.findBySkinIdAndRecordedAtBetweenOrderByRecordedAtDesc(skinId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "currentPrices", key = "#skinId")
    @Transactional(readOnly = true)
    public PriceDTO getCurrentPrice(Long skinId) {
        if (!skinRepository.existsById(skinId)) {
            throw new RuntimeException("Skin not found with id: " + skinId);
        }

        PriceHistory latestPrice = priceHistoryRepository.findLatestPriceBySkinId(skinId);
        if (latestPrice == null) {
            throw new RuntimeException("No price data available for skin id: " + skinId);
        }

        return convertToDTO(latestPrice);
    }

    @Cacheable(value = "priceStats", key = "#skinId + '-' + #startDate + '-' + #endDate")
    @Transactional(readOnly = true)
    public PriceStatsDTO getPriceStats(Long skinId, LocalDateTime startDate, LocalDateTime endDate) {
        Skin skin = skinRepository.findById(skinId)
                .orElseThrow(() -> new RuntimeException("Skin not found with id: " + skinId));

        PriceHistory latestPrice = priceHistoryRepository.findLatestPriceBySkinId(skinId);
        Double avgPrice = priceHistoryRepository.findAveragePriceBySkinIdAndDateRange(skinId, startDate, endDate);
        Double minPrice = priceHistoryRepository.findMinPriceBySkinIdAndDateRange(skinId, startDate, endDate);
        Double maxPrice = priceHistoryRepository.findMaxPriceBySkinIdAndDateRange(skinId, startDate, endDate);

        List<PriceHistory> dataPoints = priceHistoryRepository.findBySkinIdAndRecordedAtBetweenOrderByRecordedAtDesc(
                skinId, startDate, endDate);

        PriceStatsDTO stats = new PriceStatsDTO();
        stats.setSkinId(skin.getId());
        stats.setSkinName(skin.getMarketHashName());
        stats.setCurrentPrice(latestPrice != null ? latestPrice.getPrice() : null);
        stats.setAveragePrice(avgPrice);
        stats.setMinPrice(minPrice);
        stats.setMaxPrice(maxPrice);
        stats.setDataPoints(dataPoints.size());

        return stats;
    }

    private PriceDTO convertToDTO(PriceHistory priceHistory) {
        return new PriceDTO(
                priceHistory.getId(),
                priceHistory.getSkin().getId(),
                priceHistory.getSkin().getMarketHashName(),
                priceHistory.getPrice(),
                priceHistory.getVolume(),
                priceHistory.getSource(),
                priceHistory.getRecordedAt()
        );
    }
}