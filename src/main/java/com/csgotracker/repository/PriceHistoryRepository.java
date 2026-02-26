package com.csgotracker.repository;

import com.csgotracker.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    // Найти всю историю цен для скина
    List<PriceHistory> findBySkinIdOrderByRecordedAtDesc(Long skinId);

    // Найти историю цен за период
    List<PriceHistory> findBySkinIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            Long skinId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Последняя цена скина
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.skin.id = :skinId ORDER BY ph.recordedAt DESC LIMIT 1")
    PriceHistory findLatestPriceBySkinId(Long skinId);

    // Средняя цена за период
    @Query("SELECT AVG(ph.price) FROM PriceHistory ph WHERE ph.skin.id = :skinId AND ph.recordedAt BETWEEN :startDate AND :endDate")
    Double findAveragePriceBySkinIdAndDateRange(Long skinId, LocalDateTime startDate, LocalDateTime endDate);

    // Минимальная цена за период
    @Query("SELECT MIN(ph.price) FROM PriceHistory ph WHERE ph.skin.id = :skinId AND ph.recordedAt BETWEEN :startDate AND :endDate")
    Double findMinPriceBySkinIdAndDateRange(Long skinId, LocalDateTime startDate, LocalDateTime endDate);

    // Максимальная цена за период
    @Query("SELECT MAX(ph.price) FROM PriceHistory ph WHERE ph.skin.id = :skinId AND ph.recordedAt BETWEEN :startDate AND :endDate")
    Double findMaxPriceBySkinIdAndDateRange(Long skinId, LocalDateTime startDate, LocalDateTime endDate);
}