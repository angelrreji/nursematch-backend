package com.nursematch.rotation.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RotationRequest {

    private String id;            // UUID, set in service layer

    private String specialty;     // "Family Practice", "Psych", "Peds"

    private String preferredCity;
    private String preferredState;
    private double lat;           // geocoded later via SmartyStreets
    private double lng;

    private LocalDate startDate;
    private LocalDate endDate;
    private int hoursRequired;

    private RequestStatus status = RequestStatus.PENDING;
}