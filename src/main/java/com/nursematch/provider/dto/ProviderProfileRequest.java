package com.nursematch.provider.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ProviderProfileRequest {

    @NotBlank
    private String credentials;

    @NotBlank
    private String licenseState;

    @NotEmpty
    private List<String> specialties;
    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;
}