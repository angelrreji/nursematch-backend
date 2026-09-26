package com.nursematch.provider.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProviderProfileRequest {

    @NotBlank
    @Size(max = 50)
    private String credentials;

    @NotBlank
    @Size(max = 50)
    private String licenseState;

    @NotEmpty
    private List<String> specialties;

    @NotBlank
    @Size(max = 200)
    private String address;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @Size(max = 50)
    private String state;
}