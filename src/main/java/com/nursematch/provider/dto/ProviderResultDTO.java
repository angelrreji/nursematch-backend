package com.nursematch.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProviderResultDTO {
    private String providerId;
    private String userId;
    private List<String> specialties;
    private String city;
    private String state;
    private double distanceMiles;
}