package com.nursematch.provider.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AvailabilitySlot {
    private LocalDate startDate;
    private LocalDate endDate;
    private int slotsOpen;
}