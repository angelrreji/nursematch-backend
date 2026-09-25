package com.nursematch.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProviderProfileResponse {
    private String id;
    private String userId;
    private String credentials;
    private String licenseState;
    private List<String> specialties;
    private String city;
    private String state;
    private boolean acceptingStudents;
    private List<AvailabilitySlotDTO> availability;
}