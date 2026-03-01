package com.csgotracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceStatsDTO {
    private Long skinId;
    private String skinName;
    private BigDecimal currentPrice;
    private Double averagePrice;
    private Double minPrice;
    private Double maxPrice;
    private Integer dataPoints;
}