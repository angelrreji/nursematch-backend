package com.nursematch.match.dto;

import com.nursematch.match.model.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MatchDetailsResponse {
    private String id;
    private String specialty;
    private LocalDate startDate;
    private LocalDate endDate;
    private MatchStatus status;
    private boolean depositPaid;
    private boolean paperworkComplete;

    // Unlocked only after payment
    private String providerCredentials;
    private String providerCity;
    private String providerState;
}