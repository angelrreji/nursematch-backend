package com.nursematch.rotation.dto;

import com.nursematch.rotation.model.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RotationRequestDTO {

    private String id;            // null on create, populated on response

    @NotBlank
    private String specialty;
    @NotBlank
    private String address;   // street address for geocoding

    @NotBlank
    private String preferredCity;

    @NotBlank
    private String preferredState;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Positive
    private int hoursRequired;

    private RequestStatus status;  // ignored on create, set by service
}