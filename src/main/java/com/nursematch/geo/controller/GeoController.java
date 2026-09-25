package com.nursematch.geo.controller;

import com.nursematch.geo.GeocodingClient;
import com.nursematch.geo.dto.GeocodeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeocodingClient geocodingClient;

    @GetMapping("/geocode")
    public ResponseEntity<GeocodeResult> geocode(
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String state) {
        return ResponseEntity.ok(geocodingClient.geocode(address, city, state));
    }
}