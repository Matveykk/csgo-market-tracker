package com.csgotracker.dto.steam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SteamPriceResponse {

    private boolean success;

    @JsonProperty("lowest_price")
    private String lowestPrice;

    @JsonProperty("volume")
    private String volume;

    @JsonProperty("median_price")
    private String medianPrice;
}