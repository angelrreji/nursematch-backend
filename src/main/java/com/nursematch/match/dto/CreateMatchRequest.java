package com.nursematch.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMatchRequest {

    @NotBlank
    private String providerId;

    @NotBlank
    private String rotationRequestId;

    @NotBlank
    private String specialty;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}