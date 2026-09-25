package com.nursematch.geo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeocodeResult {
    private double lat;
    private double lng;
    private String city;
    private String state;
}